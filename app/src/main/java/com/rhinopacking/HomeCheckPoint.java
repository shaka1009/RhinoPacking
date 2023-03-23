package com.rhinopacking;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.rhinopacking.DB.SQL;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Fecha;
import com.rhinopacking.models.CheckPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeCheckPoint extends AppCompatActivity {

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
        setContentView(R.layout.activity_home_checkpoint);
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
            Intent intent = new Intent(this, HomeCheckPointAgregar.class);
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
    private void table(CheckPoint mStatus)
    {
        TableRow tableRow;
        /*TableLayout.LayoutParams tableRowParams= new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);

        int leftMargin=0;
        int topMargin=10;
        int rightMargin=0;
        int bottomMargin=10;

        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);*/

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

        String sStatus = "";
        if(mStatus.isCheckpoint_df())
        {
            tableRow.setBackgroundResource(R.drawable.table_rojo_claro);
            sStatus = "DF";
            fecha.setText(mStatus.getFechaString(mStatus.getFecha_df()));

        }
        else if(mStatus.isCheckpoint_gdl())
        {
            tableRow.setBackgroundResource(R.drawable.table_verde_claro);
            sStatus = "GDL";
            fecha.setText(mStatus.getFechaString(mStatus.getFecha_gdl()));
        }
        else if(mStatus.isCheckpoint_ng())
        {
            tableRow.setBackgroundResource(R.drawable.table_amarillo_claro);
            sStatus = "NG";
            fecha.setText(mStatus.getFechaString(mStatus.getFecha_ng()));
        }
        else if(mStatus.isCheckpoint_la())
        {
            tableRow.setBackgroundResource(R.drawable.table_azul_claro);
            sStatus = "LA";
            fecha.setText(mStatus.getFechaString(mStatus.getFecha_la()));
        }
        else
        {
            tableRow.setBackgroundResource(R.drawable.table_gris_claro);
        }
        status.setText(sStatus);


        codigo.setText(Integer.toString(mStatus.getCodigo()));
        medidas.setText(Float.toString(mStatus.getMedidas()));
        cajas.setText(Integer.toString(mStatus.getCajas()));

        tableRow.addView(codigo);
        tableRow.addView(medidas);
        tableRow.addView(cajas);
        tableRow.addView(status);
        tableRow.addView(fecha);

        if(Home.mUser.isAdministrador())
        {
            tableRow.setOnClickListener(view -> {
                Intent intent = new Intent(this, HomeCheckPointDetalles.class);
                intent.putExtra("id_checkpoint", Integer.toString(mStatus.getId_checkpoint()));
                someActivityResultLauncher.launch(intent);
            });
        }

        tableRow.setPadding(0,15,0,15);
        //tableRow.setLayoutParams(tableRowParams);
        tableLayout.addView(tableRow);



    }

    boolean first = true;
    private void loadDatos(int position) {
        new Thread(() -> {
            try {
                if(mSql.consultarCheckPoints(mFechas.get(position).getMes(), mFechas.get(position).getYear()))
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

                if(mFechas.size()!=0 && Home.mUser.isAdministrador())
                {
                    spinnerFecha.setEnabled(true);
                    spinnerFecha.setClickable(true);
                }

                runOnUiThread(() -> {
                    spinnerFecha.setAdapter(adaptador1);
                });




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