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

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;
    FirebaseDatabase dtb = FirebaseDatabase.getInstance();
    DatabaseReference table_user = dtb.getReference("User");
    private void bindingView() {
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
    }
    private void bindingAction() {
        btnSignIn.setOnClickListener(this::onBtnSignInClick);
    }

    private void onBtnSignInClick(View view) {
        ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please waiting....");
        mDialog.show();
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if user not exit in database
                if(snapshot.child(edtPhone.getText().toString()).exists()) {
                    //Get user information
                    mDialog.dismiss();
                    User user = snapshot.child(edtPhone.getText().toString()).getValue(User.class);
                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                        Toast.makeText(SignIn.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignIn.this, "Wrong password!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "user not exist", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_sign_in);
        bindingView();
        bindingAction();
    }
}