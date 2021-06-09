package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebaseapplication.model.User;
import com.example.firebaseapplication.model.UserResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button btnConfirm;

    private  LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadingDialog = new LoadingDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d("TAG", "onAuthStateChanged:signed_out");
            }
        };

        mapping();

        btnConfirm.setOnClickListener(v -> {
            loadingDialog.startLoading();
            User user = new User("", name.getText().toString(), email.getText().toString(), password.getText().toString(), -1);
            createAccount(user);
        });
    }

    private void mapping() {
        name = findViewById(R.id.editTextTextPersonName);
        email = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword2);
        EditText confirmPassword = findViewById(R.id.editTextTextPassword3);
        btnConfirm = findViewById(R.id.button3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(User user) {
        mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener(this, task -> {
            loadingDialog.hideLoading();
            if (!task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Error when register", Toast.LENGTH_SHORT).show();
            } else {
                user.uid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                finish();
            }
        });
    }

    private void insertUserIntoDb(User user) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userColumn = db.child("users");
        userColumn.child(user.uid).setValue(user);
        userColumn.child(user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.hideLoading();
                if (snapshot.exists()) {
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error when insertDb", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.hideLoading();
                Toast.makeText(RegisterActivity.this, "Error when insertDb", Toast.LENGTH_SHORT).show();
            }
        });
    }
}