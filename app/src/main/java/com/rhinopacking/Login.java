package com.rhinopacking;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.hbb20.CountryCodePicker;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.SnackbarError;
import com.rhinopacking.providers.UserProvider;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private LinearLayout llEmpecemos, llPassword;
    private ProgressBar pbLogin;
    private CoordinatorLayout snackbar;
    SnackbarError snackbarError;
    private EditText etTelefono, etPassword;
    private RelativeLayout rlTelefono;
    private Button btnSend;

    private PopupError mPopupError;

    UserProvider mUserProvider;

    CountryCodePicker countryCodePicker; // CUANDO SE IMPLEMENTE A OTROS PAÍSES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        declaration();
        listenner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isConnected())
            snackbarError.show("No hay conexión a internet.");
    }

    private void declaration() {
        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));
        snackbar = findViewById(R.id.snackbar_layout);
        snackbarError = new SnackbarError(snackbar, this);

        btnSend = findViewById(R.id.btnSend);
        llEmpecemos = findViewById(R.id.llEmpecemos);
        llPassword = findViewById(R.id.llPassword);
        etTelefono = findViewById(R.id.etTelefono);
        etPassword = findViewById(R.id.etPassword);
        rlTelefono = findViewById(R.id.rlTelefono);
        pbLogin = findViewById(R.id.pbLogin);
        countryCodePicker = findViewById(R.id.countryCodeHolder); // CUANDO SE IMPLEMENTE A OTROS PAÍSES countryCodePicker.getSelectedCountryCode()

        mUserProvider = new UserProvider();

        etTelefono.requestFocus();
    }



    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private boolean btnSendPress = false;
    private void listenner() {
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen)
                llEmpecemos.setVisibility(View.GONE);
            else
                llEmpecemos.setVisibility(View.VISIBLE);
        });

        btnSend.setOnClickListener(v->{
            if(btnSendPress)
                return;
            else btnSendPress = true;

            runOnUiThread(() -> loadingVisible(true));

            new Thread(() -> {
                UIUtil.hideKeyboard(Login.this); //ESCONDER TECLADO

                if (etTelefono.length() == 0)
                {
                    runOnUiThread(() -> {
                        mPopupError.setPopupError("Ingresa un número de teléfono.");
                        btnSendPress = false;
                        runOnUiThread(() -> loadingVisible(false));
                    });
                }
                else if (etTelefono.length() != 12)
                {
                    runOnUiThread(() -> {
                        mPopupError.setPopupError("El número debe contener 10 dígitos.");
                        btnSendPress = false;
                        runOnUiThread(() -> loadingVisible(false));
                    });
                }
                else if (etPassword.length() == 0)
                {
                    runOnUiThread(() -> {
                        mPopupError.setPopupError("Debes ingresar una Contraseña.");
                        btnSendPress = false;
                        runOnUiThread(() -> loadingVisible(false));
                    });
                }
                else if(!isConnected())
                {
                    runOnUiThread(() -> {
                        snackbarError.show("No hay conexión a internet.");
                        btnSendPress = false;
                        runOnUiThread(() -> loadingVisible(false));
                    });
                }
                else
                {
                    new Thread(() -> runOnUiThread(() -> mUserProvider.getDb().collection("usuarios").document("+" + countryCodePicker.getSelectedCountryCode()+etTelefono.getText().toString().replaceAll(" ", "")).get().addOnSuccessListener(documentSnapshot -> {
                        if (Objects.equals(documentSnapshot.get("password"), etPassword.getText().toString())) {
                            Intent otpIntent = new Intent(Login.this , LoginOTP.class);
                            otpIntent.putExtra("telefono", countryCodePicker.getSelectedCountryCode()+etTelefono.getText().toString().replaceAll(" ", ""));
                            startActivity(otpIntent);
                            finish();
                        }
                        else {
                            runOnUiThread(() -> {
                                mPopupError.setPopupError("No coincide el registro.");
                                btnSendPress = false;
                                runOnUiThread(() -> loadingVisible(false));
                            });
                        }
                    }).addOnFailureListener(e -> runOnUiThread(() -> {
                        snackbarError.show("No hay conexión a internet.");
                        btnSendPress = false;
                        runOnUiThread(() -> loadingVisible(false));
                    })))).start();
                }
            }).start();




        });

    }


    private void loadingVisible(boolean b)
    {
        if(b)
        {
            btnSend.setVisibility(View.INVISIBLE);
            pbLogin.setVisibility(View.VISIBLE);
            etTelefono.setEnabled(false);
        }
        else
        {
            btnSend.setVisibility(View.VISIBLE);
            pbLogin.setVisibility(View.INVISIBLE);
            etTelefono.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}