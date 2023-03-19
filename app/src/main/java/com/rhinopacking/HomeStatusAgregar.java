package com.rhinopacking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.rhinopacking.DB.SQL;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Fecha;
import com.rhinopacking.models.Status;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Calendar;
import java.util.Date;

public class HomeStatusAgregar extends AppCompatActivity {

    EditText etCodigo, etMedidas, etCantidad, etStatus;
    Button btnMenos, btnMas, btnAddStatus;
    CalendarView cvFecha;
    Fecha mFecha;

    private PopupError mPopupError;
    int cantidad;

    boolean pressButton;

    SQL mSql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_status_agregar);
        Toolbar.show(this, true);


        declaration();
        listener();
    }

    private void declaration() {
        etCodigo = findViewById(R.id.etCodigo);
        etMedidas = findViewById(R.id.etMedidas);
        etStatus= findViewById(R.id.etStatus);
        btnMenos = findViewById(R.id.btnMenos);
        btnMas = findViewById(R.id.btnMas);
        etCantidad = findViewById(R.id.etCantidad);
        cvFecha = findViewById(R.id.calendarView);
        btnAddStatus = findViewById(R.id.btnAddStatus);

        mSql = new SQL();


        Calendar cal = Calendar.getInstance();
        mFecha = new Fecha(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));

        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));

        cantidad = 1;
        pressButton = false;
    }

    @SuppressLint("SetTextI18n")
    private void listener()
    {
        btnMenos.setOnClickListener(view -> {
            UIUtil.hideKeyboard(HomeStatusAgregar.this); //ESCONDER TECLADO
            if(cantidad>1)
            {
                cantidad--;
                etCantidad.setText(String.valueOf(cantidad));
            }

            if(etCantidad.getText().toString().equals(""))
            {
                cantidad = 1;
                etCantidad.setText("1");
            }
        });

        btnMas.setOnClickListener(view -> {

            UIUtil.hideKeyboard(HomeStatusAgregar.this); //ESCONDER TECLADO

            if(etCantidad.getText().toString().equals(""))
            {
                cantidad = 1;
            }
            else if(Integer.parseInt(etCantidad.getText().toString())<=0)
                cantidad = 1;
            else if(Integer.parseInt(etCantidad.getText().toString())>0){
                cantidad = Integer.parseInt(etCantidad.getText().toString());
                cantidad++;
            }
            else
            {
                cantidad = Integer.parseInt(etCantidad.getText().toString());
            }
            etCantidad.setText(String.valueOf(cantidad));
        });

        cvFecha.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                mFecha = new Fecha(dayOfMonth, month + 1, year);
            }
        });


        btnAddStatus.setOnClickListener(view ->{
            if(pressButton)
                return;
            else pressButton = true;


            if(etCodigo.getText().toString().equals(""))
            {
                mPopupError.setPopupError("Ingresa el Código.");
            }
            else if(etMedidas.getText().toString().equals(""))
            {
                mPopupError.setPopupError("Ingresa las medidas.");
            }
            else if(etCantidad.getText().toString().equals("0"))
            {
                mPopupError.setPopupError("Ingresa cantidad de Cajas.");
            }
            else if(etStatus.getText().toString().equals(""))
            {
                mPopupError.setPopupError("Ingresa un Status.");
            } else
            {
                Status mStatus = new Status(Integer.parseInt(etCodigo.getText().toString()), Float.parseFloat(etMedidas.getText().toString()), cantidad, etStatus.getText().toString().toUpperCase(), mFecha);

                if(!(mStatus.getStatus().contains("G") && mStatus.getStatus().contains("D") && mStatus.getStatus().contains("L"))
                        && !(mStatus.getStatus().contains("L") && mStatus.getStatus().contains("A"))
                        && !(mStatus.getStatus().contains("N") && mStatus.getStatus().contains("G"))
                && !(mStatus.getStatus().contains("N") && mStatus.getStatus().contains("G"))
                && !(mStatus.getStatus().contains("D") && mStatus.getStatus().contains("F")))
                {
                    mPopupError.setPopupError("Debes ingrear un Status válido.");
                }


                new Thread(() -> {
                    if(mSql.insertarStatus(mStatus))
                    {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Status Añadito", Toast.LENGTH_SHORT).show();
                        });

                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    else
                    {
                        runOnUiThread(() -> {
                            mPopupError.setPopupError("Error de conexión.");
                        });
                        SleepButton();
                    }
                }).start();




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