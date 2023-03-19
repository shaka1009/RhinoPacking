package com.rhinopacking;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rhinopacking.DB.SQL;
import com.rhinopacking.adapters.RegistroPaquetesListAdapter;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Fecha;
import com.rhinopacking.models.RegistroPaquete;
import com.rhinopacking.models.Status;
import com.rhinopacking.models.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeStatus extends AppCompatActivity {

    SQL mSql;

    List <Fecha> mFechas;
    List<String> fechaString;

    Spinner spinnerFecha;

    TableLayout tableLayout;

    Button btnAddStatus;

    boolean fechasCargadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_status);
        Toolbar.show(this, true, "Status");

        //Home.mUser = new User("Jesus", "3319522734", false);

        declaration();
        listener();
        obtener_fechas();
    }



    @SuppressLint("SimpleDateFormat")
    private void declaration() {
        fechasCargadas = false;
        mSql = new SQL();
        mFechas = new ArrayList<>();
        spinnerFecha = findViewById(R.id.spinnerFecha);
        tableLayout = findViewById(R.id.table_layout);
        btnAddStatus = findViewById(R.id.btnAddStatus);



        spinnerFecha.setEnabled(false);
        spinnerFecha.setClickable(false);

        if(Home.mUser.isAdministrador())
        {
            btnAddStatus.setVisibility(View.VISIBLE);
        }

    }



    private void listener() {

        spinnerFecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDatos(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAddStatus.setOnClickListener(view ->{
            Intent intent = new Intent(this, HomeStatusAgregar.class);
            someActivityResultLauncher.launch(intent);
        });

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    obtener_fechas();
                }
            });


    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void table(Status mStatus)
    {
        TableRow tableRow;
        TextView codigo, medidas, cajas, status, fecha;


        codigo = new TextView(this);
        medidas = new TextView(this);
        cajas = new TextView(this);
        status = new TextView(this);
        fecha = new TextView(this);

        codigo.setGravity(Gravity.CENTER);
        medidas.setGravity(Gravity.CENTER);
        cajas.setGravity(Gravity.CENTER);
        status.setGravity(Gravity.CENTER);
        fecha.setGravity(Gravity.CENTER);
        tableRow = new TableRow(this);


        if(mStatus.getStatus().contains("G") && mStatus.getStatus().contains("D") && mStatus.getStatus().contains("L"))
        {
            tableRow.setBackgroundResource(R.drawable.table_verde_claro);
        }
        else if(mStatus.getStatus().contains("L") && mStatus.getStatus().contains("A"))
        {
            tableRow.setBackgroundResource(R.drawable.table_azul_claro);
        }
        else if(mStatus.getStatus().contains("N") && mStatus.getStatus().contains("G"))
        {
            tableRow.setBackgroundResource(R.drawable.table_amarillo_claro);
        }
        else if(mStatus.getStatus().contains("D") && mStatus.getStatus().contains("F"))
        {
            tableRow.setBackgroundResource(R.drawable.table_rojo_claro);
        }
        else
        {
            tableRow.setBackgroundResource(R.drawable.table_gris_claro);
        }



        codigo.setText(Integer.toString(mStatus.getCodigo()));
        medidas.setText(Float.toString(mStatus.getMedidas()));
        cajas.setText(Integer.toString(mStatus.getCajas()));
        status.setText(mStatus.getStatus());
        fecha.setText(new SimpleDateFormat("dd-MM-yyyy").format(mStatus.getFecha()));

        tableRow.addView(codigo);
        tableRow.addView(medidas);
        tableRow.addView(cajas);
        tableRow.addView(status);
        tableRow.addView(fecha);



        tableLayout.addView(tableRow);



    }

    boolean first = true;
    private void loadDatos(int position) {
        new Thread(() -> {
            try {
                if(mSql.consultarStatus(mFechas.get(position).getMes(), mFechas.get(position).getYear()))
                {

                    int count=tableLayout.getChildCount();
                    for(int i=0;i<count;i++)
                    {
                        int finalI = i;
                        runOnUiThread(() -> {
                            cleanTable(tableLayout);
                        });


                    }
                    for(int x=0; x<mSql.getStatus().size(); x++)
                    {
                        int finalX = x;

                        runOnUiThread(() -> {
                            table(mSql.getStatus().get(finalX));
                        });
                    }
                }
            }catch (Exception e) { Log.d("Shaka", "Error loadDatos: " + e); }

        }).start();
    }

    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    private void obtener_fechas() {
        new Thread(() -> {
            if(mSql.consultarFechas())
            {
                mFechas = mSql.getFechas();

                fechaString = new ArrayList<>();

                for(int x=0; x<mFechas.size(); x++)
                {
                    fechaString.add(mFechas.get(x).getFecha());
                }

                ArrayAdapter<String> adaptador1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fechaString);

                runOnUiThread(() -> {
                    spinnerFecha.setAdapter(adaptador1);
                });


                if(mFechas.size()!=0 && Home.mUser.isAdministrador())
                {
                    spinnerFecha.setEnabled(true);
                    spinnerFecha.setClickable(true);
                }

            }


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