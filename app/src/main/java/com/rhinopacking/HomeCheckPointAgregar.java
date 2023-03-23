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
import com.rhinopacking.models.CheckPoint;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Calendar;

public class HomeCheckPointAgregar extends AppCompatActivity {

    EditText etCodigo, etMedidas, etCantidad, etObservaciones;
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
        setContentView(R.layout.activity_home_checkpoint_agregar);
        Toolbar.show(this, true);


        declaration();
        listener();
    }

    private void declaration() {
        etCodigo = findViewById(R.id.etCodigo);
        etMedidas = findViewById(R.id.etMedidas);
        btnMenos = findViewById(R.id.btnMenos);
        btnMas = findViewById(R.id.btnMas);
        etCantidad = findViewById(R.id.etCantidad);
        cvFecha = findViewById(R.id.calendarView);
        btnAddStatus = findViewById(R.id.btnAddStatus);
        etObservaciones = findViewById(R.id.etObservaciones);

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
            UIUtil.hideKeyboard(HomeCheckPointAgregar.this); //ESCONDER TECLADO
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

            UIUtil.hideKeyboard(HomeCheckPointAgregar.this); //ESCONDER TECLADO

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

            Calendar c1 = Calendar.getInstance();



            if(etCodigo.getText().toString().equals(""))
            {
                mPopupError.setPopupError("Ingresa el Código.");
                SleepButton();
            }
            else if(etMedidas.getText().toString().equals(""))
            {
                mPopupError.setPopupError("Ingresa las medidas.");
                SleepButton();
            }
            else if(etCantidad.getText().toString().equals("0"))
            {
                mPopupError.setPopupError("Ingresa cantidad de Cajas.");
                SleepButton();
            }
            else if((mFecha.getMes()>(c1.get(Calendar.MONTH) +1))&&(mFecha.getYear()>=c1.get(Calendar.YEAR))||((mFecha.getDia() > c1.get(Calendar.DAY_OF_MONTH))&&(mFecha.getMes() == (c1.get(Calendar.MONTH) +1))&&(mFecha.getYear()==c1.get(Calendar.YEAR))))
            {
                mPopupError.setPopupError("Mes no válido.");
                SleepButton();
            }
            else
            {
               CheckPoint mStatus = new CheckPoint(Integer.parseInt(etCodigo.getText().toString()), Float.parseFloat(etMedidas.getText().toString()), cantidad, mFecha, etObservaciones.getText().toString());


                    new Thread(() -> {
                        if(mSql.insertarCheckPoint(mStatus))
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