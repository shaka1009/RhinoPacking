package com.rhinopacking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rhinopacking.DB.SQL;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.PopupFinalizar;
import com.rhinopacking.includes.SnackbarError;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Registro;

import java.io.FileOutputStream;

public class HomeFinalizar extends AppCompatActivity {
    String codigo;

    LinearLayout llSinPagar, llEfectivo, llTransferencia, llCortesia;
    RadioButton rbSinPagar, rbEfectivo, rbTransferencia, rbCortesia;

    Button btnFinalizar, btnCancelar;

    FloatingActionButton faFotoEntrega;

    private ConnectivityManager cm;

    ImageView ciFotoEntrega;

    TextView tvNombre, tvCodigo, tvPrecio;

    int metodo;

    private boolean pressButton = false;

    Bitmap bitmap;
    Bundle extras;

    SQL mSql;

    boolean cargo;

    private PopupFinalizar mPopupFinalizar;
    private CoordinatorLayout snackbar;
    SnackbarError snackbarError;

    Registro registro;

    private PopupError mPopupError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_finalizar);
        Toolbar.show(this, true, "Finalizar Servicio");

        codigo = getIntent().getStringExtra("codigo");
        declaration();
        listenner();

        mSql.close();

        cargarDatos();
    }

    @SuppressLint("SetTextI18n")
    private void cargarDatos() {
        if(mSql.getInfoFinalizar(codigo)){

            registro = mSql.getRegistro();

            tvNombre.setText(registro.getNombre());
            tvCodigo.setText(registro.getCodigo());
            tvPrecio.setText(registro.getPrecio()+"$");

            new Thread(() -> {
                if(registro.isPagado())
                {
                    if(registro.getMetodo()==1)
                    {
                        runOnUiThread(() -> {
                            rbEfectivo.setVisibility(View.GONE);
                            llEfectivo.setVisibility(View.VISIBLE);
                        });
                    }
                    else if(registro.getMetodo()==2)
                    {
                        runOnUiThread(() -> {
                            rbTransferencia.setVisibility(View.GONE);
                            llTransferencia.setVisibility(View.VISIBLE);
                        });
                    }
                    else if(registro.getMetodo()==3)
                    {
                        runOnUiThread(() -> {
                            rbCortesia.setVisibility(View.GONE);
                            llCortesia.setVisibility(View.VISIBLE);
                        });
                    }
                }
                else if(registro.getMetodo()==0){
                    runOnUiThread(() -> {
                        llSinPagar.setVisibility(View.VISIBLE);
                        llEfectivo.setVisibility(View.VISIBLE);
                        llTransferencia.setVisibility(View.VISIBLE);
                        llCortesia.setVisibility(View.VISIBLE);
                    });
                }
                cargo = true;
            }).start();
        }

    }

    private void declaration() {

        metodo = 0;

        mPopupFinalizar = new PopupFinalizar(this, getApplicationContext(), findViewById(R.id.popupError));
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnCancelar = findViewById(R.id.btnCancelar);
        faFotoEntrega = findViewById(R.id.faFotoEntrega);
        ciFotoEntrega = findViewById(R.id.ciFotoEntrega);

        tvNombre = findViewById(R.id.tvNombre);
        tvCodigo = findViewById(R.id.tvCodigo);
        tvPrecio = findViewById(R.id.tvPrecio);

        bitmap = null;


        registro = new Registro();
        mSql = new SQL();

        cargo = false;

        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));

        llSinPagar = findViewById(R.id.llSinPagar);
        llEfectivo = findViewById(R.id.llEfectivo);
        llTransferencia = findViewById(R.id.llTransferencia);
        llCortesia = findViewById(R.id.llCortesia);

        rbSinPagar = findViewById(R.id.rbSinPagar);
        rbEfectivo = findViewById(R.id.rbEfectivo);
        rbTransferencia = findViewById(R.id.rbTransferencia);
        rbCortesia = findViewById(R.id.rbCortesia);

        snackbar = findViewById(R.id.snackbar_layout);
        snackbarError = new SnackbarError(snackbar, this);
    }

    private void listenner(){

        faFotoEntrega.setOnClickListener(v->{
            if(pressButton)
                return;
            else
                pressButton = true;
            camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            SleepButton();
        });

        btnCancelar.setOnClickListener(view -> {
            if (pressButton)
                return;
            else pressButton = true;

            finish();
        });

        btnFinalizar.setOnClickListener(view -> {
            if (pressButton)
                return;
            else pressButton = true;


            if(rbSinPagar.isChecked())
                metodo=0;
            else if(rbEfectivo.isChecked())
                metodo=1;
            else if(rbTransferencia.isChecked())
                metodo=2;
            else if(rbCortesia.isChecked())
                metodo=3;




            if(bitmap == null)
            {
                mPopupError.setPopupError("Debes tomar una foto de la entrega.");
            }
            else if(rbSinPagar.isChecked()){
                mPopupFinalizar.setPopup("No has ingresado método de pago, ¿Estás seguro que no pagó?\nFinaliza para terminar el registro.");
            }
            else if(!isConnected())
            {
                runOnUiThread(() -> {
                    snackbarError.show("No hay conexión a internet.");
                });
            }
            else if(rbEfectivo.isChecked() || rbTransferencia.isChecked() || rbCortesia.isChecked())
            {
                String metodoString = "";
                if(metodo==1)
                {
                    metodoString = "Efectivo.";
                }
                else if(metodo == 2)
                {
                    metodoString = "Transferencia.";
                }
                else if(metodo == 3)
                {
                    metodoString = "Cortesía.";
                }
                String finalMetodoString = metodoString;
                runOnUiThread(() -> {
                    mPopupFinalizar.setPopup("Desear finalizar el servicio con el método de pago: " + finalMetodoString);
                });

            }






            SleepButton();
        });


        llSinPagar.setOnClickListener(view -> {
            if (pressButton)
                return;
            else pressButton = true;

            rbSinPagar.setChecked(true);
            rbEfectivo.setChecked(false);
            rbTransferencia.setChecked(false);
            rbCortesia.setChecked(false);

            pressButton = false;
        });

        llEfectivo.setOnClickListener(view -> {
            if(pressButton)
                return;
            else pressButton = true;

            rbSinPagar.setChecked(false);
            rbEfectivo.setChecked(true);
            rbTransferencia.setChecked(false);
            rbCortesia.setChecked(false);

            pressButton = false;
        });

        llTransferencia.setOnClickListener(view -> {
            if(pressButton)
                return;
            else
                pressButton = true;

            rbSinPagar.setChecked(false);
            rbEfectivo.setChecked(false);
            rbTransferencia.setChecked(true);
            rbCortesia.setChecked(false);

            pressButton = false;
        });

        llCortesia.setOnClickListener(view -> {
            if(pressButton)
                return;
            else
                pressButton = true;

            rbSinPagar.setChecked(false);
            rbEfectivo.setChecked(false);
            rbTransferencia.setChecked(false);
            rbCortesia.setChecked(true);

            pressButton = false;
        });

        mPopupFinalizar.popupFinalizarBtn.setOnClickListener(view -> {
            new Thread(() -> {
                mPopupFinalizar.hidePopupCerrarSesion();

                if((rbSinPagar.isChecked() || rbEfectivo.isChecked() || rbTransferencia.isChecked()) || rbCortesia.isChecked()){
                    if(mSql.updateFin(registro.getCodigo(), metodo, bitmap))
                    {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Entrega Finalizada.", Toast.LENGTH_SHORT).show();
                        });
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "ERROR.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                else if(registro.isPagado()){
                   if(mSql.updateFin(registro.getCodigo(), bitmap))
                    {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Entrega Finalizada.", Toast.LENGTH_SHORT).show();
                        });
                        finish();
                    }
                    else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "ERROR.", Toast.LENGTH_SHORT).show();
                        });
                        finish();
                    }
                }
            }).start();
        });
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    ActivityResultLauncher<Intent> camaraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                //ARCHIVO

                extras = result.getData().getExtras();
                bitmap = (Bitmap) extras.get("data");
                ciFotoEntrega.setImageBitmap(bitmap);

                try {
                    FileOutputStream fos = openFileOutput(crearNombreArchivoJPG(codigo), Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                }catch (Exception e){}
            }
        }
    });

    private String crearNombreArchivoJPG(String codigo)
    {
        return "ENT" + "_" + codigo;
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


    @SuppressLint("NonConstantResourceId")
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