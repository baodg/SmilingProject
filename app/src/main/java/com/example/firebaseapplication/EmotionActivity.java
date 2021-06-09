package com.example.firebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebaseapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmotionActivity extends AppCompatActivity {

    private User user;
    private int emotion = -1;
    private ImageView happy, sad, normal;
    private Button btnOk;

    private  LoadingDialog loadingDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);
        loadingDialog = new LoadingDialog(this);
        mapping();
        if (getIntent().hasExtra("USER")) {
            user = (User) getIntent().getSerializableExtra("USER");
        }

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotion = 0;
            }
        });

        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotion = 1;
            }
        });

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotion = 2;
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoading();
                user.emotion = emotion;
                updateDb(user);
            }
        });
    }

    private void mapping() {
        happy = findViewById(R.id.fun);
        sad = findViewById(R.id.imageView2);
        normal = findViewById(R.id.imageView3);
        btnOk = findViewById(R.id.button4);
    }

    private void updateDb(User user) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userColumn = db.child("users");
        userColumn.child(user.uid).setValue(user);
        userColumn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.hideLoading();
                if (snapshot.exists()) {
                    Toast.makeText(EmotionActivity.this, "Update Complete", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmotionActivity.this, "Update Failed by some reason", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.hideLoading();
                Toast.makeText(EmotionActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}