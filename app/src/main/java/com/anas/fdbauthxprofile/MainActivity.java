package com.anas.fdbauthxprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText eEmail,ePass;
    Button btnSignup,btnSkip;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        btnSignup=findViewById(R.id.btnSignup);
        btnSkip=findViewById(R.id.btnSkip);
        eEmail=findViewById(R.id.eEmail);
        ePass=findViewById(R.id.ePass);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email=eEmail.getText().toString().trim();
                String password=ePass.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();

                                } else {
                                    eEmail.setText("");
                                    ePass.setText("");
                                    Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}