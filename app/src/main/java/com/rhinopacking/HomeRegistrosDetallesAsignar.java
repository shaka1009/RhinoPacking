package com.rhinopacking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rhinopacking.DB.SQL;
import com.rhinopacking.adapters.OperadoresSpinner;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Operador;

import java.util.ArrayList;
import java.util.List;

public class HomeRegistrosDetallesAsignar extends AppCompatActivity {

    SQL mSql;
    String codigo;

    Spinner spinner;

    List<Operador> mOperadores;

    int id_operador=0;

    Button btnAsignar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_registros_detalles_asignar);
        Toolbar.show(this, true, "Asignar");

        codigo = getIntent().getStringExtra("codigo");

        declaration();
        listenner();
        loadOperadores();



    }

    private void listenner() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_operador = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAsignar.setOnClickListener(view -> {



            if(mSql.updateStatus(codigo, mOperadores.get(id_operador).getId_usuario()))
            {
                Toast.makeText(this, "Asignado a: " +mOperadores.get(id_operador).getNombre(), Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
            else {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }


        });
    }


    private void declaration() {
        mSql = new SQL();
        spinner = findViewById(R.id.spinner);
        btnAsignar = findViewById(R.id.btnAsignar);
    }

    private void loadOperadores() {

        mOperadores = new ArrayList<>();




        if(mSql.getValOperadores())
        {
            mOperadores = mSql.getmOperadores();

            Spinner spinnerDomicilio = findViewById(R.id.spinner);
            OperadoresSpinner mAdapter = new OperadoresSpinner(this, (ArrayList<Operador>) mOperadores);
            spinnerDomicilio.setAdapter(mAdapter);

        }
        else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
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