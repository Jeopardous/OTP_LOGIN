package com.example.knowtek;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String phoneNo,otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    LinearLayout linearLayout;
    Button askloginbtn,loginButton;
    ProgressBar progressBar;
    FireBaseMethods fireBaseMethods;
    Button genBtn;
    private String verificationCode;
    EditText phnEditText,otpEditText;
    BottomSheetBehavior bottomSheetBehavior;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         fireBaseMethods=new FireBaseMethods(this);
         firebaseAuth=FirebaseAuth.getInstance();
         verificationCallback();
         init();
    }
    public void init()
    {

        progressBar=findViewById(R.id.progress_circular);
        linearLayout=findViewById(R.id.bottom_sheet_linearlayout);
        bottomSheetBehavior=BottomSheetBehavior.from(linearLayout);
        askloginbtn=findViewById(R.id.ask_phone_login);
        loginButton=findViewById(R.id.login_btn);
        genBtn=findViewById(R.id.generate_otp_btn);
        phnEditText=findViewById(R.id.phone_no);
        otpEditText=findViewById(R.id.enter_otp);

    }

    public void doThis(View view)
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    public void genOtp(View view)
    {
        progressBar.setVisibility(View.VISIBLE);
        phoneNo="+91"+phnEditText.getText().toString();
        if(phoneNo.length()==13)
        {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNo,
                    60,
                    TimeUnit.SECONDS,
                    MainActivity.this,
                    mCallbacks);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            phnEditText.setError("Invalid Phone Number");
        }

    }

    private void verificationCallback()
    {
        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String getOtpCode=phoneAuthCredential.getSmsCode();
                if(getOtpCode!=null)
                {
                    otpEditText.setText(getOtpCode);
                    progressBar.setVisibility(View.VISIBLE);
                    autoVerifiCode(getOtpCode);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationCode = s;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }
    private void autoVerifiCode(String otp)
    {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp);
                signInWithPhone(credential);

    }

    private void signInWithPhone(PhoneAuthCredential phoneAuthCredential) {
            firebaseAuth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"OTP Is Correct",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("usrPhn",phoneNo);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext()," InCorrect OTP",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });

    }

    public void onLogin(View view)
    {
        otp=otpEditText.getText().toString();
        if(otp.isEmpty()||otp.length()<6)
        {
            otpEditText.setError("invalid code");
            otpEditText.requestFocus();
            return;
        }
        autoVerifiCode(otp);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("usrPhn",phoneNo);
            startActivity(intent);
        }

    }
}
