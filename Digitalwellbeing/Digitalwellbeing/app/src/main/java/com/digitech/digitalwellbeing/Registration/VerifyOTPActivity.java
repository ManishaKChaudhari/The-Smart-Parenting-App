package com.digitech.digitalwellbeing.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.digitech.digitalwellbeing.childuser.MainActivity;
import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.utiles.firebasetables;

import com.gne.www.lib.OnPinCompletedListener;
import com.gne.www.lib.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    TextView mobilenumber;
    TextView resend;
    Button verify;
    PinView pinView;
    ProgressBar progressBar;

    String verificationId;
    String number;

    String code = "";

    private static final String TAG = "VerifyOTPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        Intent intent = getIntent();
        verificationId = intent.getStringExtra("verification_id");
        number = intent.getStringExtra("number");

        mobilenumber = findViewById(R.id.mobile_text);
        resend = findViewById(R.id.resend);
        verify = findViewById(R.id.verify_otp);
        pinView = findViewById(R.id.pinview);

        progressBar = findViewById(R.id.progressBar);

        mobilenumber.setText("+91 "+number);

        pinView.setOnPinCompletionListener(new OnPinCompletedListener() {
            @Override
            public void onPinCompleted(String entirePin) {
                code = entirePin;

                progressBar.setVisibility(View.VISIBLE);
                verify.setVisibility(View.GONE);

                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                verify.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()){

                                    try {

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid());
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Intent intent1 = new Intent(VerifyOTPActivity.this, MainActivity.class);
                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent1);
                                                    finish();
                                                }
                                                else {
                                                    Intent intent1 = new Intent(VerifyOTPActivity.this, RegistrationActivity.class);
                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent1);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    } catch (Exception e) {
                                        Log.d(TAG, "onComplete: "+e.getMessage());
                                    }

                                }
                                else {
                                    Toast.makeText(VerifyOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + number,
                        60,
                        TimeUnit.SECONDS,
                        VerifyOTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);

                                Toast.makeText(VerifyOTPActivity.this, "OTP Re-sent", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code.length() < 6) {
                    Toast.makeText(VerifyOTPActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.GONE);

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    verify.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()){

                                        try {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("config_user").child(FirebaseAuth.getInstance().getUid());
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        Intent intent1 = new Intent(VerifyOTPActivity.this, MainActivity.class);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent1);
                                                        finish();
                                                    }
                                                    else {
                                                        Intent intent1 = new Intent(VerifyOTPActivity.this, RegistrationActivity.class);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent1);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } catch (Exception e) {
                                            Log.d(TAG, "onComplete: "+e.getMessage());
                                        }

                                    }
                                    else {
                                        Toast.makeText(VerifyOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

    }
}