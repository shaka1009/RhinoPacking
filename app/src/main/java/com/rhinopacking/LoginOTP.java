package com.rhinopacking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.SnackbarError;
import com.rhinopacking.providers.AuthProvider;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Locale;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class LoginOTP extends AppCompatActivity {

    private ConnectivityManager cm;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    private PopupError mPopupError;


    private String telefono;
    private String Credentials = "";

    private AuthProvider mAuthProvider;

    private OtpTextView etCodigo;

    private TextView tvInfo;

    private Button btnVerificar, btnReSend;

    private boolean btnCheckPress = false;

    private ProgressBar pbLoginOTP;

    SnackbarError snackbarError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        telefono = getIntent().getStringExtra("telefono");

        declaration();
        listenner();
        callBacks();
        send_code();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isConnected())
            snackbarError.show("No hay conexión a internet.");
    }

    private void declaration() {
        mAuthProvider = new AuthProvider();

        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));
        CoordinatorLayout snackbar = findViewById(R.id.snackbar_layout);
        snackbarError = new SnackbarError(snackbar, this);

        etCodigo = findViewById(R.id.etCodigo);
        tvInfo = findViewById(R.id.tvInfo);
        btnVerificar = findViewById(R.id.btnVerificar);
        btnReSend = findViewById(R.id.btnReSend);
        pbLoginOTP = findViewById(R.id.pbLoginOTP);

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void listenner() {
        etCodigo.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                UIUtil.hideKeyboard(LoginOTP.this); //ESCONDER TECLADO
                //verificar(); //OPCIONAL
            }
        });


        btnReSend.setOnClickListener(v -> {
            if(btnCheckPress)
                return;
            else
                btnCheckPress = true;
            tvInfo.setText("");
            loadingVisible(true);
            mAuthProvider.sendCodeVerification(LoginOTP.this, telefono, mCallBacks);
            btnReSend.setVisibility(View.INVISIBLE);
            btnCheckPress = false;
        });


        btnVerificar.setOnClickListener(v -> verificar());
    }

    private void verificar()
    {
        if(btnCheckPress)
            return;
        else btnCheckPress = true;

        runOnUiThread(() -> loadingVisible(true));
        new Thread(() -> {

            UIUtil.hideKeyboard(LoginOTP.this); //ESCONDER TECLADO

            String verification_code = etCodigo.getOTP().replaceAll(" ", "");

            if (verification_code.length() == 0)
            {
                runOnUiThread(() -> {
                    mPopupError.setPopupError("Ingresa el código que se te envió.");
                    btnCheckPress = false;
                    runOnUiThread(() -> loadingVisible(false));
                });
            }
            //else if (etCodigo.length() != 11) //CAMBIAR CUANDO ESTE LA MASK
            else if (verification_code.length() != 6)
            {
                runOnUiThread(() -> {
                    mPopupError.setPopupError("Debes ingresar los 6 dígitos.");
                    btnCheckPress = false;
                    runOnUiThread(() -> loadingVisible(false));
                });
            }
            else if(!isConnected())
            {
                runOnUiThread(() -> {
                    snackbarError.show("No hay conexión a internet.");
                    btnCheckPress = false;
                    runOnUiThread(() -> loadingVisible(false));
                });
            }
            else
            {
                new Thread(() -> {
                    if(Credentials.equals(""))
                    {
                        runOnUiThread(() -> {
                            etCodigo.setOTP("");
                            mPopupError.setPopupError("Has ingresado un código incorrecto o ya caducado.");
                            btnCheckPress = false;
                            runOnUiThread(() -> loadingVisible(false));
                        });
                    }
                    else
                    {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Credentials , verification_code);
                        signIn(credential);
                    }

                }).start();
            }
        }).start();
    }

    private void callBacks() {
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                tvInfo.setText("Error al intentar mandar el SMS, intentalo más tarde.");
                loadingVisible(false);
            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Credentials  = s;
                loadingVisible(false);
                contador();
            }


            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                //loadingVisible(false);
                //btnReSend.setVisibility(View.VISIBLE);
                //Credentials = "";
                //tvInfo.setText("El tiempo de espera ha terminado, intenta de nuevo.");
            }
        };
    }


    private void send_code() {
        new Thread(() -> {
            while(!isConnected())
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mAuthProvider.sendCodeVerification(LoginOTP.this, telefono, mCallBacks);
        }).start();
    }




    private void contador() {
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvInfo.setText(String.format(Locale.getDefault(), "Reenviar código en %d seg.", millisUntilFinished / 1000L));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                btnVerificar.setVisibility(View.INVISIBLE);
                btnReSend.setVisibility(View.VISIBLE);
                Credentials = "";
                tvInfo.setText("El tiempo de espera ha terminado, intenta de nuevo.");
            }
        }.start();
    }

    private void loadingVisible(boolean b) {

        if (b) {
            btnReSend.setVisibility(View.INVISIBLE);
            pbLoginOTP.setVisibility(View.VISIBLE);
            etCodigo.setEnabled(false);
        } else {
            btnVerificar.setVisibility(View.VISIBLE);
            pbLoginOTP.setVisibility(View.INVISIBLE);
            etCodigo.setEnabled(true);
        }
    }

    private void signIn(PhoneAuthCredential credential){
        mAuthProvider.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){


                Toast.makeText(this, "Has ingresado exitosamente.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginOTP.this, Home.class);
                startActivity(intent);
                finish();

            }else{
                mPopupError.setPopupError("Has ingresado un código incorrecto o ya caducado.");
                btnCheckPress = false;
                loadingVisible(false);
                etCodigo.setOTP("");
                etCodigo.requestFocus();
            }
        });
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }




}