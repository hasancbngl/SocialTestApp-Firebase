package com.cobanogluhasan.frebasetestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cobanogluhasan.frebasetestapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText emailEdittext, passwordEditText;
    Button signUpButton, loginButton;

    public static FirebaseAuth mAuth;

    String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpView();

        mAuth = FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser()!=null) {
            Toast.makeText(this, "you alredy signed in!", Toast.LENGTH_SHORT).show();
            moveOn();
        }


    }

    public void signUpClicked(final View view) {
        email = emailEdittext.getText().toString();
        password = passwordEditText.getText().toString();

        if (email.equals("") || password.equals("")) {

            Toast.makeText(this, "email and password required!.", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //add to database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference();


                        reference.child("users").child(Objects.requireNonNull(Objects.requireNonNull(task.getResult())
                                .getUser()).getUid()).child("email").setValue(email);


                        moveOn();
                        Log.i(TAG, "createUserWithEmail:success");
                        Toast.makeText(MainActivity.this, "successfully signed up! ",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "task.getException().getMessage()" +"Failed you have registrated account.Please Log In! ",
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onComplete: " + task.getException().getMessage());
                    }
                }
            });

        }
    }


    public void loginClicked(View view) {
        email = emailEdittext.getText().toString();
        password = passwordEditText.getText().toString();

        if (email.equals("") || password.equals("")) {

            Toast.makeText(this, "email and password required!.", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "onComplete: " + "login Successfull");
                                moveOn();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed. There's no account matched. Please first sign Up ",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    private void setUpView() {

        emailEdittext = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);
    }

    private void moveOn() {
        Intent intent = new Intent(this, SnapsActivity.class);
        startActivity(intent);

    }


}