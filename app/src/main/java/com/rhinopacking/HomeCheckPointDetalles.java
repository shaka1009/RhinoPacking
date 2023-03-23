package com.rhinopacking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.rhinopacking.includes.Toolbar;

public class HomeCheckPointDetalles extends AppCompatActivity {

    String id_checkpoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_checkpoint_detalles);
        Toolbar.show(this, true, "Agregar Registro");

        id_checkpoint = getIntent().getStringExtra("id_checkpoint");

        Toast.makeText(this, "ID: "+ id_checkpoint, Toast.LENGTH_SHORT).show();
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