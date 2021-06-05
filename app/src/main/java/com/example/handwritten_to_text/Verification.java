package com.example.handwritten_to_text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {

    EditText otp1,otp2,otp3,otp4,otp5,otp6;
    TextView phone;
    Button verifyOTP;
    String sentOTP;
    ProgressBar progressBar;

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
        setContentView(R.layout.activity_verification);

        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);
        otp5=findViewById(R.id.otp5);
        otp6=findViewById(R.id.otp6);
        verifyOTP=findViewById(R.id.verifyotp);
        phone=findViewById(R.id.mobile);
        progressBar=findViewById(R.id.progressbar2);

        phone.setText(String.format(
                "+91-%s",getIntent().getStringExtra("mobile")
        ));

        sentOTP = getIntent().getStringExtra("sentOTP");

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!otp1.getText().toString().trim().isEmpty() && !otp2.getText().toString().trim().isEmpty() && !otp3.getText().toString().trim().isEmpty() && !otp4.getText().toString().trim().isEmpty() && !otp5.getText().toString().trim().isEmpty() && !otp6.getText().toString().trim().isEmpty()){
                    String enteredOTP=otp1.getText().toString()
                                     +otp2.getText().toString()
                                     +otp3.getText().toString()
                                     +otp4.getText().toString()
                                     +otp5.getText().toString()
                                     +otp6.getText().toString();
                    if(sentOTP!=null){
                        progressBar.setVisibility(View.VISIBLE);
                        verifyOTP.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(
                                sentOTP,enteredOTP
                        );
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    verifyOTP.setVisibility(View.VISIBLE);

                                    if(task.isSuccessful()){
                                        SharedPreferences preferences = getSharedPreferences("remember",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("saveinfo","true");
                                        editor.apply();
                                        Intent intent=new Intent(getApplicationContext(),MainApp.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(Verification.this,"Incorrect OTP entered",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                    else{
                        Toast.makeText(Verification.this,"Please Check your internet connection and try again!",Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(Verification.this,"Verifying OTP",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Verification.this,"Enter all digits of OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
        otpreader();

        findViewById(R.id.resendotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private  void otpreader(){
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
