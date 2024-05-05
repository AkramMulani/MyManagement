package com.ak.mymanagement.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ak.mymanagement.R;
import com.ak.mymanagement.dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private boolean SHOW = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String name = username.getText().toString();
        String pass = password.getText().toString();

        boolean usernameValid = false;
        boolean passwordValid = false;

        if (name.isEmpty()) {
            username.setError("Username is required to login");
        }
        else if (name.split("@").length == 2) {
            // example@gmail.com
            usernameValid = true;
        }
        else {
            username.setError("Invalid username found. please provide correct email id i.e. example.com");
        }
        if (pass.isEmpty()) {
            password.setError("Password is required to login");
        }
        else if (pass.length()<6) {
            password.setError("Password should be greater than 6 characters");
        }
        else {
            // Password is valid
            passwordValid = true;
        }

        if (usernameValid && passwordValid) {
            AuthCredential credential = EmailAuthProvider.getCredential(name, pass);
            auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = task.getResult().getUser();
                        assert user != null;
                        if (Objects.equals(user.getDisplayName(), Objects.requireNonNull(auth.getCurrentUser()).getDisplayName())) {
                            Toast.makeText(LoginActivity.this, "Authentication Successful!", Toast.LENGTH_SHORT).show();
                            // run dashboard
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            if (auth.getCurrentUser()!=null) {
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed! Please Try After Some Time", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void signup(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void toggle(View view) {
        EditText password = findViewById(R.id.password);
        FloatingActionButton togglePassword = findViewById(R.id.toggle_password);
        if (SHOW) {
            SHOW = false;
            // Hide password
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            togglePassword.setImageResource(R.drawable.baseline_key_off_24);
        } else {
            SHOW = true;
            // Show password
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            togglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
        }
    }
}
