package com.rhinopacking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rhinopacking.DB.SQL;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.CheckPoint;
import com.rhinopacking.models.Fecha;

import java.util.Calendar;

public class HomeCheckPointDetalles extends AppCompatActivity {

    String id_checkpoint;

    CheckPoint checkPoint;
    LinearLayout llCalendario;

    TextView tvCheckpointLA, tvCheckpointNG, tvCheckpointGDL, tvCheckpointDF, tvCodigo, tvMedidas, tvCajas;

    CalendarView calendarView;
    Fecha mFecha;

    Button btnUpdateCheckpoint;

    SQL mSql;

    boolean pressButton;

    private PopupError mPopupError;

    String nameCheckFuture = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_checkpoint_detalles);
        Toolbar.show(this, true, "CheckPoint");

        id_checkpoint = getIntent().getStringExtra("id_checkpoint");
        Toast.makeText(this, "ID: "+ id_checkpoint, Toast.LENGTH_SHORT).show();
        declaration();
        listener();
        consultarCheckPoint();

    }




    private void declaration() {
        mSql = new SQL();

        llCalendario = findViewById(R.id.llCalendario);
        tvCheckpointLA = findViewById(R.id.tvCheckpointLA);
        tvCheckpointNG= findViewById(R.id.tvCheckpointNG);
        tvCheckpointGDL= findViewById(R.id.tvCheckpointGDL);
        tvCheckpointDF = findViewById(R.id.tvCheckpointDF);
        tvCodigo = findViewById(R.id.tvCodigo);
        tvMedidas = findViewById(R.id.tvMedidas);
        tvCajas= findViewById(R.id.tvCajas);

        calendarView= findViewById(R.id.calendarView);

        btnUpdateCheckpoint = findViewById(R.id.btnUpdateCheckpoint);

        Calendar cal = Calendar.getInstance();
        mFecha = new Fecha(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));

        pressButton = false;

        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));
    }

    private void listener() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> mFecha = new Fecha(dayOfMonth, month + 1, year));

        btnUpdateCheckpoint.setOnClickListener(view ->{
            if(pressButton)
                return;
            else pressButton = true;
            Calendar c1 = Calendar.getInstance();

            if((mFecha.getMes()>(c1.get(Calendar.MONTH) +1))&&(mFecha.getYear()>=c1.get(Calendar.YEAR))||((mFecha.getDia() > c1.get(Calendar.DAY_OF_MONTH))&&(mFecha.getMes() == (c1.get(Calendar.MONTH) +1))&&(mFecha.getYear()==c1.get(Calendar.YEAR))))
            {
                mPopupError.setPopupError("Fecha no válida.");
                SleepButton();
            }
            else {


                if(checkPoint.isCheckpoint_df())
                    nameCheckFuture = "";
                else if(checkPoint.isCheckpoint_gdl())
                    nameCheckFuture = "df";
                else if(checkPoint.isCheckpoint_ng())
                    nameCheckFuture = "gdl";
                else if(checkPoint.isCheckpoint_la())
                    nameCheckFuture = "ng";


                if( mSql.updateCheckPoint(checkPoint.getId_checkpoint(), nameCheckFuture, mFecha.getSqlFecha()))
                {
                    Toast.makeText(this, "CheckPoint Actualizado.", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else
                {
                    mPopupError.setPopupError("Error de conexión.");
                    SleepButton();
                }

            }

        });
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

    @SuppressLint("SetTextI18n")
    private void consultarCheckPoint() {
        if(mSql.consultarCheckPoint(Integer.parseInt(id_checkpoint)))
        {
            checkPoint = mSql.getmCheckpoint();

            if(checkPoint.isCheckpoint_df())
            {
                tvCheckpointDF.setVisibility(View.VISIBLE);
                setTextViewDrawableColor(tvCheckpointDF, R.color.RojoClaro);
                tvCheckpointDF.setText("DF - " + checkPoint.getFechaString(checkPoint.getFecha_df()));
            }
            if(checkPoint.isCheckpoint_gdl())
            {
                tvCheckpointGDL.setVisibility(View.VISIBLE);
                setTextViewDrawableColor(tvCheckpointGDL, R.color.VerdeClaro);
                tvCheckpointGDL.setText("Guadalajara - " + checkPoint.getFechaString(checkPoint.getFecha_gdl()));

            }
            if(checkPoint.isCheckpoint_ng())
            {
                tvCheckpointNG.setVisibility(View.VISIBLE);
                setTextViewDrawableColor(tvCheckpointNG, R.color.AmarilloClaro);
                tvCheckpointNG.setText("Nogales - " + checkPoint.getFechaString(checkPoint.getFecha_ng()));
            }
            if(checkPoint.isCheckpoint_la())
            {
                tvCheckpointLA.setVisibility(View.VISIBLE);
                setTextViewDrawableColor(tvCheckpointLA, R.color.AzulClaro);
                tvCheckpointLA.setText("Los Ángeles - " + checkPoint.getFechaString(checkPoint.getFecha_la()));
            }

            tvCodigo.setText(Integer.toString(checkPoint.getCodigo()));

            tvMedidas.setText(Float.toString(checkPoint.getMedidas()));
            tvCajas.setText(Integer.toString(checkPoint.getCajas()));


            if(!checkPoint.isCheckpoint_df())
            {
                btnUpdateCheckpoint.setVisibility(View.VISIBLE);
                llCalendario.setVisibility(View.VISIBLE);
            }


        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getColor(color), PorterDuff.Mode.SRC_IN));
            }
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