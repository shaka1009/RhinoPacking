package com.rhinopacking;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rhinopacking.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {


    private AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.Theme_RhinoPacking_Loading);
        declarations();



    }

    private void declarations() {
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onStart() {
        super.onStart();


        if(mAuthProvider.existSession())
        {
            startActivity(new Intent(MainActivity.this, Home.class));
            //startActivity(new Intent(MainActivity.this, HomeStatusAgregar.class));
            finish();
        }
        else
        {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }


    }
}