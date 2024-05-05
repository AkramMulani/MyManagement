package com.ak.mymanagement.auth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.Toast;

import com.ak.mymanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference refUsers;
    private boolean SHOW = false;

    EditText username;
    EditText mobile;
    EditText email;
    EditText password;
    EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.sign_username);
        mobile = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email_id);
        password = findViewById(R.id.sign_password);
        confirmPassword = findViewById(R.id.confirm_password);

        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            username.getWindowInsetsController().hide(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            username.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public void register(View view) {
        String name = username.getText().toString();
        String mobileNo = mobile.getText().toString();
        String emailId = email.getText().toString();
        String pass = password.getText().toString();
        String confirmPass = confirmPassword.getText().toString();

        db = FirebaseDatabase.getInstance();
        refUsers = db.getReference("Users");
        
        boolean ok = validate(name, mobileNo, emailId, pass, confirmPass);
        
        if (ok) {
            auth.createUserWithEmailAndPassword(emailId, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                        saveUser(name, mobileNo, emailId, pass);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Something went wrong! Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveUser(String name, String mobileNo, String emailId, String pass) {
        refUsers.child(user.getUid()).child("name").setValue(name);
        refUsers.child(user.getUid()).child("mobile").setValue(mobileNo);
        refUsers.child(user.getUid()).child("email").setValue(emailId);
        refUsers.child(user.getUid()).child("password").setValue(hashPassword(pass));
        finish();
    }

    private boolean validate(String name, String mobileNo, String emailId, String pass, String confirmPass) {
        boolean usernameValid = false;
        boolean mobileNoValid = false;
        boolean emailIdValid = false;
        boolean passValid = false;
        boolean confirmPassValid = false;
        
        if (name.isEmpty()) {
            username.setError("Username is required");
        } else if (name.length()<5) {
            username.setError("Username should be greater than 5 characters");
        } else {
            usernameValid = true;
        }
        
        if (mobileNo.isEmpty()) {
            mobile.setError("Mobile No. is required");
        } else if (mobileNo.length() != 10) {
            mobile.setError("Mobile No. should be 10 numbers");
        } else {
            mobileNoValid = true;
        }
        
        if (emailId.isEmpty()) {
            email.setError("Email ID is required");
        } else if (emailId.split("@").length != 2) {
            email.setError("Email ID is invalid");
        } else {
            emailIdValid = true;
        }
        
        if (pass.isEmpty()) {
            password.setError("Password is required");
        } else if (pass.length()<6) {
            password.setError("Password should be greater than 6 characters");
        } else {
            passValid = true;
        }
        
        if (confirmPass.isEmpty()) {
            confirmPassword.setError("Please confirm your password!");
        } else if (!confirmPass.equals(pass)) {
            confirmPassword.setError("Couldn't match your password!");
        } else {
            confirmPassValid = true;
        }
        
        return usernameValid && mobileNoValid && emailIdValid && passValid && confirmPassValid;
    }

    public void login(View view) { finish(); }

    public void toggle(View view) {
        FloatingActionButton togglePassword = findViewById(R.id.sign_toggle_password);
        if (SHOW) {
            SHOW = false;
            // Hide password
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            togglePassword.setImageResource(R.drawable.baseline_key_off_24);
        } else {
            SHOW = true;
            // Show password
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            togglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
        }
    }

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Add password bytes to digest
            md.update(password.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            // Convert byte array to Base64 representation
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
