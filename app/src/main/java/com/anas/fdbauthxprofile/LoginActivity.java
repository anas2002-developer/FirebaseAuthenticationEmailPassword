package com.anas.fdbauthxprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText eEmail,ePass;
    Button btnLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        btnLogin=findViewById(R.id.btnLogin);
        eEmail=findViewById(R.id.eEmail);
        ePass=findViewById(R.id.ePass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=eEmail.getText().toString().trim();
                String password=ePass.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                    Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();

                                } else {
                                    eEmail.setText("");
                                    ePass.setText("");
                                    Toast.makeText(LoginActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}