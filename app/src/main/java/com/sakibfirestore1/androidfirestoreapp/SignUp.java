package com.sakibfirestore1.androidfirestoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedHashMap;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUp";


    private TextInputLayout usernameLayout;
    private TextInputLayout phoneNumberLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText username;
    private TextInputEditText phoneNumber;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
    private TextView signin;

    private Button createAccount;

    private LinkedHashMap<String, String> signUpData = new LinkedHashMap<>();

    String errorMessage = "Invalid input";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameLayout = (TextInputLayout) findViewById(R.id.usernameLayout);
        phoneNumberLayout = (TextInputLayout) findViewById(R.id.phoneLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.confirmPasswordLayout);

        username = (TextInputEditText) findViewById(R.id.usernameid);
        phoneNumber = (TextInputEditText) findViewById(R.id.phoneid);
        password = (TextInputEditText) findViewById(R.id.passwordid);
        confirmPassword = (TextInputEditText) findViewById(R.id.confirmPasswordid);

        createAccount = (Button) findViewById(R.id.createAccountid);

        signin = (TextView) findViewById(R.id.signinid);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignIn();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUsername(extractUsername())) {
                    if (!signUpData.containsKey(extractUsername()))
                        usernameLayout.setError(null);
                        signUpData.put("username", extractUsername());
                } else {
                    usernameLayout.setError(errorMessage);
                }

                if (validatePhoneNumber(extractPhoneNumber())) {
                    if (!signUpData.containsKey(extractPhoneNumber()))
                        signUpData.put("phoneNumber", "+880" + extractPhoneNumber());
                } else {
                    phoneNumberLayout.setError(errorMessage);
                }

                if (validatePassword(extractPassword())) {
                    if (!signUpData.containsKey(extractPassword()))
                        signUpData.put("password", extractPassword());
                } else {
                    passwordLayout.setError(errorMessage);
                }

                if (!validateConfirmPassword(extractPassword(), extractConfirmPassword())) {
                    confirmPasswordLayout.setError("Passwords don't match");
                }
                else{
                    if (signUpData.size() == 3) {
                        addDataInFireStoreDatabase(db, signUpData);
                        //going back to login
                        backToLogin();
                    }
                }
                for(String data:signUpData.values()){
                    Log.d(TAG,"signup data: "+data);
                }
            }
        });

    }

    void openSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intent);
    }

    void backToLogin() {
        Intent i = new Intent(getApplicationContext(), SignIn.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    String extractUsername() {
        return username.getText().toString();
    }

    String extractPhoneNumber() {
        return phoneNumber.getText().toString().trim();
    }

    String extractPassword() {
        return password.getText().toString();
    }

    String extractConfirmPassword() {
        return confirmPassword.getText().toString();
    }

    boolean validateUsername(String username) {
        if (username.length() == 0 || username.length() > 30) return false;
        return true;
    }

    boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 0 || phoneNumber.length() > 10) return false;
        return true;
    }

    boolean validatePassword(String password) {
        if (password.length() == 0 || password.length() > 20) return false;
        return true;
    }

    boolean validateConfirmPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }
        return false;
    }

    void addDataInFireStoreDatabase(FirebaseFirestore db, LinkedHashMap user) {
        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}