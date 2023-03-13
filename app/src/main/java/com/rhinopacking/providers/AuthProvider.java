package com.rhinopacking.providers;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthProvider {
    FirebaseAuth mAuth;

    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    public String getId() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }

    public boolean existSession() {
        return mAuth.getCurrentUser() != null;
    }

    public void logout() {
        mAuth.signOut();
    }

    public void sendCodeVerification(@NonNull Activity activity, String telefono, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+" + telefono)       // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public Task<AuthResult> signInWithCredential(PhoneAuthCredential credential) {
        return mAuth.signInWithCredential(credential);
    }

    public String getTelefono(){
       return Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
    }
}
