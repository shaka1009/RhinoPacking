package com.rhinopacking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rhinopacking.DB.SQL;
import com.rhinopacking.adapters.RegistroGuiasListAdapter;
import com.rhinopacking.adapters.RegistroPaquetesListAdapter;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.PopupMostrarFoto;
import com.rhinopacking.includes.SnackbarError;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Registro;
import com.rhinopacking.models.RegistroGuia;
import com.rhinopacking.models.RegistroPaquete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeRegistrosAgregar extends AppCompatActivity {



    RegistroPaquetesListAdapter registrosPaqueteListAdapter;

    RegistroGuiasListAdapter registrosGuiaListAdapter;



    private boolean pressButton = false;
    TextView tvCodigo;

    SQL mSql;

    LinearLayout llSinPagar, llEfectivo, llTransferencia, llCortesia;
    RadioButton rbSinPagar, rbEfectivo, rbTransferencia, rbCortesia;
    Button btnAgregarRegistro;

    private ConnectivityManager cm;
    FloatingActionButton faFotoBodega;

    ImageView ciFotoBodega;

    ImageButton ibAddPaquete, ibAddGuia;

    String codigo;


    Bitmap bitmap;
    Bundle extras;

    private RecyclerView mRecyclerPaquetes;
    private RecyclerView mRecyclerGuias;

    public static List<RegistroPaquete> mRegistrosPaquetes;
    public static List<RegistroGuia> mRegistrosGuias;

    private CoordinatorLayout snackbar;
    SnackbarError snackbarError;
    private PopupError mPopupError;


    EditText etNombre, etTelefono, etCosto, etObservaciones;

    PopupMostrarFoto mPopupMostrarFoto;




    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_registros_agregar);
        Toolbar.show(this, true, "Agregar Registro");

        declaration();
        genCodigo();
        listenner();

        ActivityCompat.requestPermissions(HomeRegistrosAgregar.this,PERMISSIONS_STORAGE,30);

    }

    private void declaration() {

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        tvCodigo = findViewById(R.id.tvCodigo);

        mSql = new SQL();

        bitmap = null;

        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etCosto = findViewById(R.id.etCosto);
        etObservaciones = findViewById(R.id.etObservaciones);

        llSinPagar = findViewById(R.id.llSinPagar);
        llEfectivo = findViewById(R.id.llEfectivo);
        llTransferencia = findViewById(R.id.llTransferencia);
        llCortesia = findViewById(R.id.llCortesia);
        rbSinPagar = findViewById(R.id.rbSinPagar);
        rbEfectivo = findViewById(R.id.rbEfectivo);
        rbTransferencia = findViewById(R.id.rbTransferencia);
        rbCortesia = findViewById(R.id.rbCortesia);

        ciFotoBodega = findViewById(R.id.ciFotoBodega);
        faFotoBodega = findViewById(R.id.faFotoBodega);
        ibAddPaquete = findViewById(R.id.ibAddPaquete);


        mRegistrosPaquetes = new ArrayList<>();
        mRegistrosGuias = new ArrayList<>();

        mRecyclerPaquetes = findViewById(R.id.rvPaquetes);
        mRecyclerGuias = findViewById(R.id.rvGuias);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerPaquetes.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        mRecyclerGuias.setLayoutManager(linearLayoutManager1);

        btnAgregarRegistro = findViewById(R.id.btnAgregarRegistro);

        ibAddGuia = findViewById(R.id.ibAddGuia);

        //barras de error
        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));
        snackbar = findViewById(R.id.snackbar_layout);
        snackbarError = new SnackbarError(snackbar, this);

        mPopupMostrarFoto = new PopupMostrarFoto(this, getApplicationContext(), findViewById(R.id.popupMostrarFoto));
    }

    @SuppressLint("SuspiciousIndentation")
    private void listenner() {

        ciFotoBodega.setOnClickListener(view ->{
            try {
                if(bitmap!=null)
                    mPopupMostrarFoto.setPopupFoto(bitmap);
            }catch (Exception e){}
        });
        llSinPagar.setOnClickListener(view -> {
            if (pressButton)
                return;
            else pressButton = true;

            rbSinPagar.setChecked(true);
            rbEfectivo.setChecked(false);
            rbTransferencia.setChecked(false);

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

        faFotoBodega.setOnClickListener(v->{
            if(pressButton)
                return;
            else
                pressButton = true;


            PickImage();
            //camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

            SleepButton();
        });

        ibAddPaquete.setOnClickListener(v->{
            Intent intent = new Intent(this, HomeRegistrosAgregarPaquete.class);
            someActivityResultLauncher.launch(intent);
        });

        ibAddGuia.setOnClickListener(v->{
            PickImageGuias();
        });

        btnAgregarRegistro.setOnClickListener(v->{

            if(pressButton)
                return;
            else pressButton = true;

            new Thread(() -> {
                if(bitmap == null)
                {
                    runOnUiThread(() -> {
                        mPopupError.setPopupError("Toma la foto del registro.");
                    });
                    SleepButton();
                }
                else if(etNombre.getText().toString().equals(""))
                {
                    runOnUiThread(() -> {
                        mPopupError.setPopupError("Debes ingresar al menos el Nombre.");
                    });
                    SleepButton();
                }
                else if (etTelefono.length() !=0 && etTelefono.length() != 10)
                {

                    runOnUiThread(() -> {
                        mPopupError.setPopupError("El número de teléfono debe ser de 10 dígitos.");
                    });
                    SleepButton();
                }
                else if(mRegistrosPaquetes.size()==0)
                {

                    runOnUiThread(() -> {
                        mPopupError.setPopupError("Debes agregar al menos 1 paquete.");
                    });
                    SleepButton();
                }
                else if(!isConnected())
                {

                    runOnUiThread(() -> {
                        snackbarError.show("No tienes conexión a internet.");
                    });
                    SleepButton();
                }
                else {
                    float precio=0;

                    if(!etCosto.getText().toString().equals(""))
                        precio = Float.parseFloat(etCosto.getText().toString());


                    int metodo=0;

                    if(rbEfectivo.isChecked())
                        metodo=1;
                    else if(rbTransferencia.isChecked())
                        metodo=2;
                    else if(rbCortesia.isChecked())
                        metodo=3;

                    Registro registro = new Registro(codigo, etNombre.getText().toString(), etTelefono.getText().toString(), precio, !rbSinPagar.isChecked(), metodo, "EN_BODEGA", etObservaciones.getText().toString(), bitmap);


                    if(mSql.addRegistro(registro, mRegistrosPaquetes, mRegistrosGuias))
                    {
                        saveImagen(bitmap);

                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                        });

                        finish();
                    }
                    else {
                        snackbarError.show("No hay conexión con el Servidor.");
                        SleepButton();
                    }

                }


            }).start();


        });

    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    private void PickImage() {
        ImagePicker.Companion.with(HomeRegistrosAgregar.this)
                .crop()
                .cropSquare()
                .maxResultSize(Home.RESOLUCION,Home.RESOLUCION)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntent(intent ->{
                    foto_bodega.launch(intent);
                    return null;
                });
                //.start();
    }

    private void PickImageGuias() {
        ImagePicker.Companion.with(HomeRegistrosAgregar.this)
                .crop()
                .cropSquare()
                .maxResultSize(Home.RESOLUCION,Home.RESOLUCION)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntent(intent ->{
                    foto_guia.launch(intent);
                    return null;
                });
        //.start();
    }

    ActivityResultLauncher<Intent> foto_guia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri uri=result.getData().getData();
                    //imageView.setImageURI(uri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                        //ciFotoBodega.setImageBitmap(bitmap);




                        RegistroGuia registroGuia = new RegistroGuia(codigo, bitmap);
                        mRegistrosGuias.add(registroGuia);


                        registrosGuiaListAdapter = new RegistroGuiasListAdapter(mRegistrosGuias, this);

                        registrosGuiaListAdapter.setOnItemClickListener(new RegistroGuiasListAdapter.OnItemClickListener() {
                            @Override
                            public void onDeleteClick(int position) {
                                removeItemGuias(position);
                            }
                        });

                        mRecyclerGuias.setHasFixedSize(true);
                        mRecyclerGuias.setLayoutManager(new LinearLayoutManager(this));
                        mRecyclerGuias.setAdapter(registrosGuiaListAdapter);

                        Toast.makeText(this, "Total: " + mRegistrosGuias.size(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e) {}

                    SleepButton();
                }
            });

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri=data.getData();
            //imageView.setImageURI(uri);



            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                ciFotoBodega.setImageBitmap(bitmap);
            }catch (Exception e) {}




            SleepButton();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            SleepButton();
        } else {
            SleepButton();
        }
    }*/

    private void saveImagen(Bitmap bitmap)
    {
        try {
            File mainDir= new File(Environment.getExternalStorageDirectory().getPath(), "RhinoPacking");
            //File mainDir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"", "RhinoPacking");
            if (!mainDir.exists()) {
                if(mainDir.mkdirs())
                {
                    Log.d("Shaka", "Dir creado:"+ mainDir);
                }

                else
                {
                    Log.d("Shaka", "Dir No creado:"+ mainDir);
                }
            }
            else
            {
                Log.d("Shaka", "existe" + mainDir);
            }

            String imageName = crearNombreArchivoJPG(codigo);
            File file = new File(mainDir, imageName);
            OutputStream out;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            }catch (Exception e)
            {
                Log.d("SHAKA", "Error: " + e);
            }

        }

        catch (Exception e) {
            Log.d("SHAKA", "Error: " + e);
        }
    }

    private String crearNombreArchivoJPG(String codigo)
    {
        return codigo + "_REC" + ".jpg";
    }





    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    Uri uri = Uri.parse(data.getStringExtra("foto_paquete"));

                    Bitmap bitmap2 = null;
                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    RegistroPaquete registroPaquete = new RegistroPaquete(codigo, Integer.parseInt(data.getStringExtra("cantidad")), data.getStringExtra("medidas") ,  bitmap2);
                    mRegistrosPaquetes.add(registroPaquete);

                    registrosPaqueteListAdapter = new RegistroPaquetesListAdapter(mRegistrosPaquetes, this);

                    registrosPaqueteListAdapter.setOnItemClickListener(new RegistroPaquetesListAdapter.OnItemClickListener() {
                        @Override
                        public void onDeleteClick(int position) {
                            removeItem(position);
                        }
                    });

                    mRecyclerPaquetes.setHasFixedSize(true);
                    mRecyclerPaquetes.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerPaquetes.setAdapter(registrosPaqueteListAdapter);

                }
            });



    ActivityResultLauncher<Intent> foto_bodega = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Uri uri=result.getData().getData();
                //imageView.setImageURI(uri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                    ciFotoBodega.setImageBitmap(bitmap);
                }catch (Exception e) {}

            }
        });

    public void removeItem(int position) {
        mRegistrosPaquetes.remove(position);
        registrosPaqueteListAdapter.notifyItemRemoved(position);
        mRecyclerPaquetes.setHasFixedSize(true);
    }

    public void removeItemGuias(int position) {
        mRegistrosGuias.remove(position);
        registrosGuiaListAdapter.notifyItemRemoved(position);
        mRecyclerGuias.setHasFixedSize(true);
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








    private void genCodigo() {

        new Thread(() -> {
            codigo = "BOD";
            int numero;
            ArrayList numeros = new ArrayList();


            for (int i = 1; i <= 7; i++) {
                numero = (int) (Math.random() * 50 + 1);
                if (numeros.contains(numero)) {
                    i--;
                } else {
                    codigo = codigo + numero;
                }
            }

            while(true)
            {
                if(!mSql.valCodigo(codigo))
                {
                    break;
                }
            }

            runOnUiThread(() -> tvCodigo.setText(codigo));

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