package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebaseapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btn_ok;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private  LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingDialog = new LoadingDialog(this);
        mapping();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d("TAG", "onAuthStateChanged:signed_out");
            }
        };
        btn_ok.setOnClickListener(v -> signIn(email.getText().toString(), password.getText().toString()));
    }

    private void mapping() {
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btn_ok = findViewById(R.id.button);
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


    private void signIn(String email, String password) {
        loadingDialog.startLoading();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
            if (!task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            } else {
                String uid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
//                handleUid(uid);
                startActivity(new Intent(LoginActivity.this, ListUserActivity.class));
            }
        });
    }

    private void handleUid(String uId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query userColumn = db.child("users").orderByChild(uId);

        userColumn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingDialog.hideLoading();
                User user = dataSnapshot.child(uId).getValue(User.class);
                if (user != null) {
                    Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, EmotionActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "No Account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.hideLoading();
                Toast.makeText(LoginActivity.this, "Error Realtime DataBase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}