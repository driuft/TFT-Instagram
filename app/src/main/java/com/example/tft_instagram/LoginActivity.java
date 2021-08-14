package com.example.tft_instagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    Button btLogin;
    RelativeLayout rlSignUp;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null){
            goToMainActivity();
        }

        btLogin = findViewById(R.id.btLogin);
        rlSignUp= findViewById(R.id.rlSignUp);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);

        rlSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPassword.getText()) == true || TextUtils.isEmpty(etUsername.getText()) == true){
                    if(TextUtils.isEmpty(etPassword.getText()) == true){
                        etPassword.setError("Password cannot be empty");
                    }
                    if(TextUtils.isEmpty(etUsername.getText()) == true){
                        etUsername.setError("Username cannot be empty");
                    }
                    return;
                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(etUsername.getText().toString());
                    user.setPassword(etPassword.getText().toString());
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                goToMainActivity();
                            }else{
                                Log.e(TAG, "Exception: " + e.getMessage());
                                return;
                            }
                        }
                    });
                }
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPassword.getText()) == true || TextUtils.isEmpty(etUsername.getText()) == true){
                    if(TextUtils.isEmpty(etPassword.getText()) == true){
                        etPassword.setError("Password cannot be empty");
                    }
                    if(TextUtils.isEmpty(etUsername.getText()) == true){
                        etUsername.setError("Username cannot be empty");
                    }
                    return;
                } else {
                    ParseUser.logInInBackground(etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(user != null){
                                goToMainActivity();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Cannot login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}