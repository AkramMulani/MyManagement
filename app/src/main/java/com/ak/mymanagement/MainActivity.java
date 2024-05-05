package com.ak.mymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ak.mymanagement.auth.LoginActivity;
import com.ak.mymanagement.dashboard.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser()!=null) {
            // User already logged in... goto dashboard
            startActivity(new Intent(this, DashboardActivity.class));
            if (auth.getCurrentUser()!=null) {
                finish();
            }
        }
        else {
            // Ask for login first
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
