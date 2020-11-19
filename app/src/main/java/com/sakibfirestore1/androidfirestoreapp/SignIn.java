package com.sakibfirestore1.androidfirestoreapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class SignIn extends AppCompatActivity {

    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText username;
    private TextInputEditText password;
    private Button login;

    private TextView signup;

    private static final String TAG = "SignIn";

    String errorMessage = "Invalid input";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LinkedList<User> users = new LinkedList<>();

    @Override
    protected void onResume() {
        super.onResume();
        users.clear();
        getDataFromFireStoreDatabase(db);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getDataFromFireStoreDatabase(db);

        usernameLayout = (TextInputLayout) findViewById(R.id.usernameLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);

        username = (TextInputEditText) findViewById(R.id.usernameid);
        password = (TextInputEditText) findViewById(R.id.passwordid);

        login = (Button) findViewById(R.id.loginid);
        signup = (TextView) findViewById(R.id.signupid);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateUsername(extractUsername())){
                    usernameLayout.setError(null);
                }
                else{
                    usernameLayout.setError(errorMessage);
                }

                if(validatePassword(extractPassword())){
                    passwordLayout.setError(null);
                }
                else{
                    passwordLayout.setError(errorMessage);
                }

                if(validateUsername(extractUsername()) && validatePassword(extractPassword())){
                    if(validateUser(extractUsername(),extractPassword())){
                        myToast(R.layout.authorized,getApplicationContext());
                    }
                    else{
                        myToast(R.layout.unauthorized,getApplicationContext());
                    }
                }
            }
        });
    }

    void openSignUp() {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    String extractUsername() {
        return username.getText().toString();
    }

    String extractPassword() {
        return password.getText().toString();
    }

    boolean validateUsername(String username) {
        if (username.length() == 0 || username.length() > 30) return false;
        return true;
    }

    boolean validatePassword(String password) {
        if (password.length() == 0 || password.length() > 20) return false;
        return true;
    }

    void getDataFromFireStoreDatabase(FirebaseFirestore db) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    boolean validateUser(String username,String password){
        for(User user:users){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    void myToast(int myLayout, Context context){
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(myLayout, null);
        toast.setView(view);
        toast.show();
    }
}