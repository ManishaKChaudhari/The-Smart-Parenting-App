package com.digitech.digitalwellbeing.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.digitech.digitalwellbeing.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.concurrent.TimeUnit;

public class MobileVerificationActivity extends AppCompatActivity {

    Button getOTP;
    ProgressBar progressBar;
    EditText mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);

        mobileNumber = findViewById(R.id.mobile_number);
        progressBar = findViewById(R.id.progressBar);
        getOTP = findViewById(R.id.get_otp);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileNumber.getText().toString().trim().isEmpty()) {
                    mobileNumber.setError("Field cannot be empty");
                }
                else if (mobileNumber.getText().toString().trim().length() < 10) {
                    mobileNumber.setError("Invalid Mobile Number");
                }
                else {

                    progressBar.setVisibility(View.VISIBLE);
                    getOTP.setVisibility(View.GONE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + mobileNumber.getText().toString().trim(),
                            60,
                            TimeUnit.SECONDS,
                            MobileVerificationActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar.setVisibility(View.GONE);
                                    getOTP.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.GONE);
                                    getOTP.setVisibility(View.VISIBLE);
                                    Toast.makeText(MobileVerificationActivity.this, e.getMessage()+" 555", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(verificationId, forceResendingToken);
                                    progressBar.setVisibility(View.GONE);
                                    getOTP.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(MobileVerificationActivity.this, VerifyOTPActivity.class);
                                    intent.putExtra("number", mobileNumber.getText().toString().trim());
                                    intent.putExtra("verification_id", verificationId);
                                    startActivity(intent);
                                }
                            }
                    );

                }
            }
        });

    }
}