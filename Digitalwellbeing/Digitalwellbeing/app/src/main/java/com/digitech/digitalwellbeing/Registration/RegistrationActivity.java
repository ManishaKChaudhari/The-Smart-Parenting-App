package com.digitech.digitalwellbeing.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.childuser.MainActivity;
import com.digitech.digitalwellbeing.parentuser.ParentsDashboard;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    EditText nameET, numberET;
    String token;
    Button register;
    Button registeraschild;

    String nameStr, numberStr, pinStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameET = findViewById(R.id.name);
        numberET = findViewById(R.id.number);

        register = findViewById(R.id.register);
        registeraschild = findViewById(R.id.registeraschild);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameStr = nameET.getText().toString().trim();
                numberStr = numberET.getText().toString().trim();

                if (nameStr.isEmpty()) {
                    nameET.setError("Field cannot be empty");
                } else if (numberStr.isEmpty()) {
                    numberET.setError("Field cannot be empty");
                } else {
                    registerToDB(1);
                }

            }
        });

        registeraschild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameStr = nameET.getText().toString().trim();
                numberStr = numberET.getText().toString().trim();

                if (nameStr.isEmpty()) {
                    nameET.setError("Field cannot be empty");
                } else if (numberStr.isEmpty()) {
                    numberET.setError("Field cannot be empty");
                } else {
                    registerToDB(0);
                }

            }
        });

    }

    private void registerToDB(int i) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        token = "No token";
                    } else {
                        token = task1.getResult();
                    }

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid());
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("name", nameStr);
                    userData.put("number", numberStr);
                    userData.put("type", i + "");
                    userData.put("token", token);

                    databaseReference.setValue(userData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent1;
                            if (i == 1) {
                                intent1 = new Intent(RegistrationActivity.this, ParentsDashboard.class);
                            } else {
                                intent1 = new Intent(RegistrationActivity.this, MainActivity.class);
                            }

                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent1);
                            finish();
                        }
                    });

                });
                }
    }