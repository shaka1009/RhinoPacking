package com.rhinopacking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.PopupMostrarFoto;
import com.rhinopacking.includes.Toolbar;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.regex.Pattern;

public class HomeRegistrosAgregarPaquete extends AppCompatActivity {

    Button btnMenos, btnMas, btnAgregarPaquete;
    EditText etCantidad, etMedidas;

    int cantidad;

    ImageView ciFotoPaquete;

    Bundle extras=null;

    FloatingActionButton faFotoPaquete;

    private PopupError mPopupError;

    Bitmap bitmap = null;

    ConstraintLayout clFoto;

    PopupMostrarFoto mPopupMostrarFoto;

    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_registros_agregar_paquete);
        Toolbar.show(this, true, "Agregar Paquete");

        declaration();
        listenner();

        ActivityCompat.requestPermissions(HomeRegistrosAgregarPaquete.this,PERMISSIONS_STORAGE,30);
    }

    private void declaration() {
        cantidad = 1;

        btnMenos = findViewById(R.id.btnMenos);
        btnMas = findViewById(R.id.btnMas);
        etCantidad = findViewById(R.id.etCantidad);
        etMedidas = findViewById(R.id.etMedidas);
        ciFotoPaquete = findViewById(R.id.ivPaquete);
        btnAgregarPaquete = findViewById(R.id.btnAgregarPaquete);
        faFotoPaquete = findViewById(R.id.faFotoPaquete);

        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));

        clFoto = findViewById(R.id.clFoto);

        mPopupMostrarFoto = new PopupMostrarFoto(this, getApplicationContext(), findViewById(R.id.popupMostrarFoto));
    }

    private void listenner() {
        clFoto.setOnClickListener(view ->{
            try {
                if(bitmap!=null)
                    mPopupMostrarFoto.setPopupFoto(bitmap);
            }catch (Exception e){}
        });

        btnMenos.setOnClickListener(view -> {
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

            if(etCantidad.getText().toString().equals(""))
            {
                cantidad = 1;
            }
            else if(Integer.parseInt(etCantidad.getText().toString())<0)
                cantidad = 1;
            else if(Integer.parseInt(etCantidad.getText().toString())>0){
                cantidad++;
            }
            else
            {
                cantidad = Integer.parseInt(etCantidad.getText().toString());
            }


            etCantidad.setText(String.valueOf(cantidad));
        });

        faFotoPaquete.setOnClickListener(view ->{
            PickImage();
            //camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        });


        btnAgregarPaquete.setOnClickListener(view -> {

            if(bitmap==null)
                mPopupError.setPopupError("Debes tomar la foto del paquete.");
            else if(!(Integer.parseInt(etCantidad.getText().toString())>=1))
                mPopupError.setPopupError("Ingresa una cantidad igual o mayor a 1.");
            else if(etMedidas.getText().toString().equals(""))
                mPopupError.setPopupError("Ingresa una medida");

            //else if(!Pattern.matches("[0-9]+[xX][0-9]+[xX][0-9]+", etMedidas.getText().toString())) // de uno
            else if(!Pattern.matches("([0-9]+[xX][0-9]+[xX][0-9]+)([-][0-9]+[xX][0-9]+[xX][0-9]+)*", etMedidas.getText().toString()))
                mPopupError.setPopupError("El formato de las medidas debe de ser de 3 dimensiones separando los números con una 'x'. O si son más dimensiones, separadas con un '-'.");
            else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("cantidad", etCantidad.getText().toString().replaceAll("X", "x"));
                resultIntent.putExtra("medidas", etMedidas.getText().toString());
                resultIntent.putExtra("foto_paquete", uri.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }



        });

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen)
                clFoto.setVisibility(View.GONE);
            else
                clFoto.setVisibility(View.VISIBLE);
        });

    }

    private void PickImage() {
        ImagePicker.Companion.with(HomeRegistrosAgregarPaquete.this)
                .crop()
                .cropSquare()
                .maxResultSize(Home.RESOLUCION,Home.RESOLUCION)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .start();
    }
    Uri uri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            uri=data.getData();
            //imageView.setImageURI(uri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                ciFotoPaquete.setImageBitmap(bitmap);

            }catch (Exception e) {}
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }




    ActivityResultLauncher<Intent> camaraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                extras = result.getData().getExtras();
                bitmap = (Bitmap) extras.get("data");
                ciFotoPaquete.setImageBitmap(bitmap);
            }
        }
    });




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