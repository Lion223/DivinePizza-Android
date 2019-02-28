package com.github.lion223.divinepizza;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView userInfo;
    Button signOut;
    Button getInfo;
    CustomToast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Вітаємо Вас!");

        userInfo = findViewById(R.id.textView);
        signOut = findViewById(R.id.sign_out);
        getInfo = findViewById(R.id.get_user);

        getInfo.setOnClickListener(this);
        signOut.setOnClickListener(this);

        toast = new CustomToast(Color.rgb(28,28,28), Color.WHITE, this, new Toast(getApplicationContext()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sign_out:
                signOut();
                break;

            case R.id.get_user:
                getInfo();
                break;

            default:
                break;
        }
        getInfo();
    }

    private void getInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            toast.show("User is in");
            userInfo.setText(user.getPhoneNumber());
        } else {
            toast.show("User is none");
        }
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        userInfo.setText("Signed out");
        toast.show("Signed out");
    }

}
