package com.example.handwritten_to_text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPage extends AppCompatActivity {
    EditText phNo;
    Button getOTP;
    private long backPressedTime;
    private Toast backToast;

    @Override
    public void onBackPressed() {
        if(backPressedTime+2000>System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast=Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime=System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        phNo=findViewById(R.id.phNo);
        getOTP=findViewById(R.id.getotp);
        final ProgressBar progressBar=findViewById(R.id.progressbar);

        SharedPreferences preferences=getSharedPreferences("remember",MODE_PRIVATE);
        String remembers = preferences.getString("saveinfo","");
        if(remembers.equals("true")){
            Intent intent = new Intent(LoginPage.this,MainApp.class);
            startActivity(intent);
        }

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phNo.getText().toString().trim().isEmpty()){
                    if((phNo.getText().toString().trim()).length()==10){
                        progressBar.setVisibility(View.VISIBLE);
                        getOTP.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + phNo.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                LoginPage.this,
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
                                        Toast.makeText(LoginPage.this,"Please check your internet connection and try again!",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        super.onCodeSent(s, forceResendingToken);
                                        progressBar.setVisibility(View.GONE);
                                        getOTP.setVisibility(View.VISIBLE);
                                        Intent intent=new Intent(getApplicationContext(),Verification.class);
                                        intent.putExtra("mobile",phNo.getText().toString());
                                        intent.putExtra("sentOTP",s);
                                        startActivity(intent);

                                    }
                                }
                        );
                        /*Intent intent=new Intent(getApplicationContext(),Verification.class);
                        intent.putExtra("mobile",phNo.getText().toString());
                        startActivity(intent);*/
                    }
                    else {
                        Toast.makeText(LoginPage.this,"Enter a valid 10 digit mobile number",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginPage.this,"Enter Mobile Number",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
