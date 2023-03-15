package com.example.holayummy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.holayummy.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    EditText edtPhone, edtName, edtPassword,edtSecureCode;
    Button btnSignUp;
    FirebaseDatabase dtb = FirebaseDatabase.getInstance();
    DatabaseReference table_user = dtb.getReference("User");
    private void bindingView() {
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtSecureCode = findViewById(R.id.edtSecureCode);
    }
    private void bindingAction() {
        btnSignUp.setOnClickListener(this::onBtnSignUpClick);

    }

    private void onBtnSignUpClick(View view) {
        ProgressDialog mDialog = new ProgressDialog(SignUp.this);
        mDialog.setMessage("Please waiting....");
        mDialog.show();
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(edtPhone.getText().toString()).exists()) {
                    //Get user information
                    mDialog.dismiss();
                    Toast.makeText(SignUp.this, "Phone number already exist", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    User user = new User(edtName.getText().toString(),
                            edtPassword.getText().toString(),
                            edtSecureCode.getText().toString());
                    table_user.child(edtPhone.getText().toString()).setValue(user);
                    Toast.makeText(SignUp.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindingView();
        bindingAction();
    }
}