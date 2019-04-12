package com.example.knowtek;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

public class FireBaseMethods {

    FirebaseAuth firebaseAuth;
    Context context;

    public FireBaseMethods(Context context) {
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();

    }

    public void signInWithPhone(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(context,"OTP Verified",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
