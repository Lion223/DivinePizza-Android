package com.github.lion223.divinepizza;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity implements PopDialog.PopDialogListener {

    // елементи вікна
    EditText editTextPhoneNumber;
    EditText editTextConfirmCode;
    TextView textViewRepetitionCode;
    ProgressBar progressBar;

    // змінні Firebase
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseFirestore db;
    private CollectionReference users;
    private DocumentReference docRef;

    // змінні, які зберігають значення елементів вікна
    private String phoneNumber;
    private String username;
    private String sendConfirmCode;
    private String mVerificationId;
    private CustomToast cToast;
    private CountDownTimer countDownTimer;

    private Boolean firstTimeEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initCallbacks();

        editTextPhoneNumber = findViewById(R.id.phone_number);
        editTextConfirmCode = findViewById(R.id.confirm_code);
        textViewRepetitionCode = findViewById(R.id.repeat_code);
        progressBar = findViewById(R.id.progressBar);


        Typeface montserrat = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Regular.otf");
        editTextPhoneNumber.setTypeface(montserrat);
        editTextConfirmCode.setTypeface(montserrat);

        textViewRepetitionCode.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        cToast = new CustomToast(Color.rgb(28,28,28), Color.WHITE, this, new Toast(getApplicationContext()));


        editTextPhoneNumber.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_NEXT ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                if(isPhoneNumberCorrect()){
                                    editTextConfirmCode.setEnabled(true);
                                    if(countDownTimer == null){
                                        sendVerificationCode(phoneNumber);
                                    }else{
                                        cToast.show("Очікуйте кінця затримки відправки коду");
                                    }

                                }
                                else{
                                    editTextConfirmCode.setEnabled(false);
                                    cToast.show("Введіть мобільний номер");
                                    return true;
                                }
                                return false;
                            }
                        }
                        return true;
                    }
                }
        );

        editTextConfirmCode.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_NEXT ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                if(isConfirmCodeCorrect()){
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, sendConfirmCode);
                                    signInWithPhoneAuthCredential(credential);
                                }
                                else{
                                    cToast.show("Введіть код підтвердження");
                                    return true;
                                }
                                return false;
                            }
                        }
                        return true;
                    }
                }
        );

        textViewRepetitionCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mResendToken != null)
                    resendVerificationCode(phoneNumber, mResendToken);
                else {
                    cToast.show("Повторна відправка коду на даний момент недоступна");
                    onBackPressed();
                }
            }
        });
    }

    // провірка введеного мобільного номеру
    private boolean isPhoneNumberCorrect(){
        phoneNumber = editTextPhoneNumber.getText().toString();
        if(phoneNumber.length() == 18 && phoneNumber.substring(0,5).equals("+38(0")){
            return true;
        }
        else{
            return false;
        }
    }

    // провірка введеного верифікаційного коду
    private boolean isConfirmCodeCorrect(){
        sendConfirmCode = editTextConfirmCode.getText().toString();
        if(sendConfirmCode.length() == 6){
            return true;
        }
        else{
            return false;
        }
    }

    // отримання стану верифікаційного процесу
    private void initCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // верифікаційний код відправлено
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.i("onCodeSent", verificationId);
                Log.i("token", forceResendingToken.toString());
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

                cToast.show("Код підтвердження відправлено");
                if(textViewRepetitionCode.getVisibility() != View.VISIBLE){
                    textViewRepetitionCode.setVisibility(View.VISIBLE);
                }
                verificationTimer();
            }
            // верифікація завершена
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i("onVerificationCompleted", phoneAuthCredential.toString());
                cToast.show("Верифікація успішна");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
            // верифікація невдала
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i("onVerificationFailed", e.getMessage());
                cToast.show(e.getMessage());
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //addUserToDb();
                    if(firstTimeEnter){
                        openDialog();
                    }
                    else{
                        Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        cToast.show("Невірний код підтвердження");
                    }
                }
            }
        });
    }

    private void sendVerificationCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
        isUserNew(new FirebaseCallback() {
            @Override
            public void onCallback(Object data) {
                firstTimeEnter = (boolean) data;
            }
        });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken mResendToken){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks,
                mResendToken
        );
        verificationTimer();
    }

    private void verificationTimer(){
        if (countDownTimer == null){
            textViewRepetitionCode.setEnabled(false);
            countDownTimer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    textViewRepetitionCode.setText("Повторно відправити код (" + millisUntilFinished / 1000 + ")");
                }

                public void onFinish() {
                    textViewRepetitionCode.setText("Повторно відправити код");
                    textViewRepetitionCode.setEnabled(true);
                    countDownTimer = null;
                }

            };
            countDownTimer.start();
        }
    }

    private void isUserNew(@NonNull final FirebaseCallback firebaseCallback){
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        docRef = users.document(phoneNumber);
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    firebaseCallback.onCallback(!document.exists());
                } else {
                    Log.d("Firebase document search", "get failed with ", task.getException());
                }
            }
        });
    }

    private void addUserToDb(){
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        docRef = users.document(phoneNumber);
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("phone_number", phoneNumber);
                        user.put("name", username);
                        users.document(phoneNumber).set(user);

                    }
                } else {
                    Log.d("Firebase document search", "get failed with ", task.getException());
                }
            }
        });
    }

    private void openDialog(){
        if(firstTimeEnter){
            PopDialog dialog = new PopDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(),"Pop dialog");
        }
    }

    private Task<Boolean> isFirstTime(final String phoneNumber){
        final TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        docRef = users.document(phoneNumber);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        tcs.setResult(true);
                    }
                } else {
                    Log.d("Firebase document search", "get failed with ", task.getException());
                    tcs.setException(task.getException());
                }
            }
        });
        return tcs.getTask();
    }

    @Override
    public void applyName(String name) {
        username = name;
    }

    @Override
    public void finishIntent() {
        Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
        startActivity(intent);
        finish();
    }
}


