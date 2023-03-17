package com.rhinopacking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.rhinopacking.includes.PopupCerrarSesion;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.User;
import com.rhinopacking.providers.AuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePerfil extends AppCompatActivity {


    CircleImageView ciFotoPerfil;
    TextView tvPuesto, tvNombre, tvTelefono;
    Button btnCerrarSesion;

    private PopupCerrarSesion mPopupCerrarSesion;

    private AuthProvider mAuthProvider;


    private boolean pressButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_perfil);
        Toolbar.show(this, true);

        declaration();
        listener();
    }

    private void declaration() {
        tvPuesto = findViewById(R.id.tvPuesto);
        ciFotoPerfil = findViewById(R.id.ciFotoPerfil);
        tvNombre = findViewById(R.id.tvNombre);
        tvTelefono = findViewById(R.id.tvTelefono);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        mPopupCerrarSesion = new PopupCerrarSesion(this, getApplicationContext(), findViewById(R.id.popupError));
        mAuthProvider = new AuthProvider();

        pressButton = false;

        if(Home.mUser.isAdministrador())
            tvPuesto.setText("Administrador");
        else
            tvPuesto.setText("Operador");

        tvNombre.setText(Home.mUser.getNombre());
        tvTelefono.setText(Home.mUser.getTelefono());



    }

    private void listener()
    {
        btnCerrarSesion.setOnClickListener(view->
        {
            if(pressButton)
                return;
            else pressButton = true;

            mPopupCerrarSesion.setPopupCerrarSesion(Home.mUser.getNombre());

            SleepButton();
        });

        mPopupCerrarSesion.popupCerrarSesionSalir.setOnClickListener(v -> new Thread(() -> {
            mPopupCerrarSesion.hidePopupCerrarSesion();
            if(pressButton)
                return;
            else pressButton = true;


            try{
                deleteFile("Acc_App");
            }catch(Exception ignored){}


            mAuthProvider.logout();
            Intent intent = new Intent(HomePerfil.this, Login.class);
            startActivity(intent);
            SleepButton();
            finish();
        }).start());

    }

    private void SleepButton()
    {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pressButton = false;
        }).start();
    }


    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        finish();
    }
    //BACK PRESS
}