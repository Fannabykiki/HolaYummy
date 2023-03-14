package com.example.holayummy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    private Button btnSignUp;
    private Button btnSignIn;
    private TextView txtSlogan;


    private void bindingView() {
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtSlogan = findViewById(R.id.txtSlogan);
    }
    private void bindingAction() {
        btnSignUp.setOnClickListener(this::onBtnSignUpClick);
        btnSignIn.setOnClickListener(this::onBtnSignInClick);
    }

    private void onBtnSignInClick(View view) {
        Intent signIn = new Intent(MainActivity.this,SignIn.class);
        startActivity(signIn);
    }

    private void onBtnSignUpClick(View view) {
        Intent signUp = new Intent(MainActivity.this,SignUp.class);
        startActivity(signUp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindingView();
        bindingAction();
    }
}