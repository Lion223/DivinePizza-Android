package com.github.lion223.divinepizza.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lion223.divinepizza.CustomToast;
import com.github.lion223.divinepizza.FirebaseCallback;
import com.github.lion223.divinepizza.PopDialog;
import com.github.lion223.divinepizza.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.common.base.CharMatcher;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements PopDialog.PopDialogListener {

    // елементи вікна
    private EditText editTextPhoneNumber;
    private EditText editTextConfirmCode;
    private TextView textViewRepetitionCode;

    // змінні Firebase
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseFirestore db;
    private CollectionReference users;
    private DocumentReference docRef;
    private FirebaseUser user;

    // змінні, які зберігають значення елементів вікна
    private String phoneNumber;
    private String username;
    private String sendConfirmCode;
    private String mVerificationId;
    private CustomToast cToast;
    private CountDownTimer countDownTimer;

    private final String FB_TAG = "Firebase: ";
    private Boolean firstTimeEnter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        initCallbacks();

        editTextPhoneNumber = findViewById(R.id.login_phone_number);
        editTextConfirmCode = findViewById(R.id.login_confirm_code);
        textViewRepetitionCode = findViewById(R.id.login_repeat_code);

        textViewRepetitionCode.setVisibility(View.INVISIBLE);

        cToast = new CustomToast(Color.rgb(28,28,28), Color.WHITE,
                this, new Toast(getApplicationContext()));


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
                                    isUserNew(new FirebaseCallback() {
                                        @Override
                                        public void onCallback(Object data) {
                                            firstTimeEnter = (boolean) data;
                                        }
                                    });
                                    editTextConfirmCode.setEnabled(true);
                                    if(countDownTimer == null){
                                        sendVerificationCode(phoneNumber);
                                    }
                                    else{
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
                                    PhoneAuthCredential credential =
                                            PhoneAuthProvider.getCredential(mVerificationId, sendConfirmCode);
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
                }
            }
        });
    }

    // провірка введеного мобільного номеру
    private boolean isPhoneNumberCorrect(){
        phoneNumber = editTextPhoneNumber.getText().toString();
        if(phoneNumber.length() == 18 && phoneNumber.substring(0,5).equals("+38(0")){
            phoneNumber = CharMatcher.anyOf("( -)").removeFrom(phoneNumber);
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
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try {
                        if(firstTimeEnter){
                            openDialog();
                        }
                        else if(!firstTimeEnter){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (NullPointerException e) {
                        cToast.show("Спробуйте ще раз");
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
                    textViewRepetitionCode
                            .setText("Повторно відправити код (" + millisUntilFinished / 1000 + ")");
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
        users.whereEqualTo("phone_number", phoneNumber)
            .get()
            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot qs = task.getResult();
                        firebaseCallback.onCallback(qs.getDocuments().isEmpty());
                    }
                }
            });
    }

    private void openDialog(){
        PopDialog dialog = new PopDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(),"Pop dialog");
    }

    @Override
    public void applyName(String name) {
        username = name;
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(FB_TAG, "User profile updated.");
                        }
                    }
                });
    }

    @Override
    public void addToDb() {
        addUserToDb(phoneNumber);
    }

    private void addUserToDb(final String phoneNumber){
        Map<String, Object> user = new HashMap<>();
        user.put("phone_number", phoneNumber);
        user.put("name", username);
        users.document().set(user).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}




