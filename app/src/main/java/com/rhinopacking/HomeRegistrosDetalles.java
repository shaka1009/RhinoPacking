package com.rhinopacking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.rhinopacking.DB.SQL;
import com.rhinopacking.adapters.RegistroGuiasGridAdapter;
import com.rhinopacking.adapters.RegistroGuiasListAdapter;
import com.rhinopacking.adapters.RegistroPaquetesListAdapter;
import com.rhinopacking.includes.PopupEliminar;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.PopupMostrarFoto;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Registro;
import com.rhinopacking.models.RegistroGuia;
import com.rhinopacking.models.RegistroPaquete;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeRegistrosDetalles extends AppCompatActivity {


    Bitmap bitmap;
    Registro registro;

    String codigo;

    LinearLayout llProgress;

    SQL mSql;

    TextView tvStatus;

    TextView tvCodigo, tvSemana, tvFechaBodega, tvFechaEntrega;

    ImageView ivFotoBodega, ivFotoEntrega;

    EditText etNombre, etTelefono, etCosto, etObservaciones, etNombreOperador;

    LinearLayout linearlayoutLoad, llSinPagar, llEfectivo, llTransferencia, llObservaciones, llCortesia;

    List<RegistroPaquete> mRegistrosPaquetes;
    List<RegistroGuia> mRegistrosGuias;
    RegistroPaquetesListAdapter registrosPaqueteListAdapter;
    RegistroGuiasListAdapter registrosGuiaListAdapter;
    private RecyclerView mRecyclerPaquetes;
    private RecyclerView mRecyclerGuias;

    Button btnChangeStatus, btnFinalizar;

    private boolean pressButton;

    CardView cvOperador, cvGuias;

    PopupEliminar mPopup;
    PopupMostrarFoto mPopupMostrarFoto;

    private PopupError mPopupError;

    ScrollView scrollview;


    RegistroGuiasGridAdapter mRegistroGuiasGridAdapter;
    GridView gvGuias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_registros_detalles);
        Toolbar.show(this, true, "Detalles");

        codigo = getIntent().getStringExtra("codigo");

        //Toast.makeText(this, "Código: " + codigo, Toast.LENGTH_SHORT).show();

        declaration();
        listenner();
        loadDatos();

    }



    private void declaration() {
        //llProgress = findViewById(R.id.llProgress);
        mSql = new SQL();
        registro = new Registro();

        bitmap = null;

        mPopup = new PopupEliminar(this, getApplicationContext(), findViewById(R.id.popupEliminar));
        scrollview = findViewById(R.id.scrollview);


        gvGuias = findViewById(R.id.gvGuias);

        tvStatus = findViewById(R.id.tvStatus);

        ivFotoBodega = findViewById(R.id.ivFotoBodega);
        ivFotoEntrega = findViewById(R.id.ivFotoEntrega);

        tvCodigo = findViewById(R.id.tvCodigo);
        tvSemana = findViewById(R.id.tvSemana);
        tvFechaBodega = findViewById(R.id.tvFechaBodega);
        tvFechaEntrega = findViewById(R.id.tvFechaEntrega);

        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etCosto = findViewById(R.id.etCosto);

        linearlayoutLoad = findViewById(R.id.linearlayoutLoad);
        llSinPagar = findViewById(R.id.llSinPagar);
        llEfectivo = findViewById(R.id.llEfectivo);
        llTransferencia = findViewById(R.id.llTransferencia);
        llCortesia = findViewById(R.id.llCortesia);
        llObservaciones = findViewById(R.id.llObservaciones);
        etObservaciones = findViewById(R.id.etObservaciones);

        mRegistrosPaquetes = new ArrayList<>();
        mRegistrosGuias = new ArrayList<>();
        mRecyclerPaquetes = findViewById(R.id.rvPaquetes);
        mRecyclerGuias = findViewById(R.id.rvGuias);

        btnChangeStatus = findViewById(R.id.btnChangeStatus);

        etNombreOperador = findViewById(R.id.etNombreOperador);

        cvOperador = findViewById(R.id.cvOperador);
        cvGuias = findViewById(R.id.cvGuias);
        habilitar(false);

        btnFinalizar = findViewById(R.id.btnFinalizar);

        mPopupMostrarFoto = new PopupMostrarFoto(this, getApplicationContext(), findViewById(R.id.popupMostrarFoto));
        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));
    }


    private void checkCodigo(String contents){
        new Thread(() -> {
            if(mSql.verificarEnCamino(contents))
            {
                Intent intent = new Intent(HomeRegistrosDetalles.this, HomeFinalizar.class);
                intent.putExtra("codigo", contents);
                //startActivity(intent);

                someActivityResultLauncher.launch(intent);
            }
            else {
                runOnUiThread(() -> {
                    if(mSql.isFinalizado())
                        Toast.makeText(this, "Servicio ya finalizado.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "No hay conexión.", Toast.LENGTH_SHORT).show();
                });
            }
            SleepButton();
        }).start();
    }

    private void listenner() {

        btnFinalizar.setOnClickListener(view ->{
            if(pressButton)
                return;
            else pressButton = true;

            checkCodigo(codigo);


        });

        btnChangeStatus.setOnClickListener(view -> {
            if(pressButton)
                return;
            else pressButton = true;

            Intent intent = new Intent(this, HomeRegistrosDetallesAsignar.class);
            intent.putExtra("codigo", codigo );


            mSql.close();
            someActivityResultLauncher.launch(intent);

            SleepButton();
        });


        mPopup.popupEliminarSi.setOnClickListener(v -> new Thread(() -> {
            mPopup.hidePopupCerrarSesion();
            if(pressButton)
                return;
            else pressButton = true;
            try{
                mSql.desactivar(codigo);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registro Eliminado.", Toast.LENGTH_SHORT).show();
                });
            }catch(Exception ignored){}



            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }).start());




        ivFotoBodega.setOnClickListener(view -> {
            if(pressButton)
                return;
            else pressButton = true;

            mPopupMostrarFoto.setPopupFoto(registro.getFoto_recibido());

            SleepButton();
        });
        ivFotoEntrega.setOnClickListener(view -> {
            if(pressButton)
                return;
            else pressButton = true;

            mPopupMostrarFoto.setPopupFoto(registro.getFoto_entrega());

            SleepButton();
        });






        mRecyclerPaquetes.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            final GestureDetector mGestureDetector = new GestureDetector(HomeRegistrosDetalles.this, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int position = recyclerView.getChildAdapterPosition(child);


                        mPopupMostrarFoto.setPopupFoto(mRegistrosPaquetes.get(position).getFoto());



                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {

            }
        });


        mRecyclerGuias.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            final GestureDetector mGestureDetector = new GestureDetector(HomeRegistrosDetalles.this, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int position = recyclerView.getChildAdapterPosition(child);


                        mPopupMostrarFoto.setPopupFoto(mRegistrosGuias.get(position).getFoto());



                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {

            }
        });

        gvGuias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HomeRegistrosDetalles.this, HomeMostrar.class);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mRegistrosGuias.get(i).getFoto().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] bytesImage = byteArrayOutputStream.toByteArray();


                intent.putExtra("img", bytesImage);
                //startActivity(intent);

                mPopupMostrarFoto.setPopupFoto(mRegistrosGuias.get(i).getFoto());
            }
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Intent data = result.getData();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();

                }
            });



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //loadDatos(); //aquí debería ser carga de status nuevo, pago, metodo
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
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
    private void loadDatos() {

        new Thread(() -> {
            if(mSql.consultarRegistro(codigo))
            {
                registro = mSql.getRegistro();


                leerStatus();
                leerFotos();

                runOnUiThread(() -> {
                    tvCodigo.setText(codigo);
                });

                runOnUiThread(() -> {
                    tvSemana.setText("Semana: " + registro.getSemana());
                });

                runOnUiThread(() -> {
                    tvFechaBodega.setText("Fecha en Bodega: " + registro.getFecha_almacen());
                });

                if(registro.getStatus().equals("FINALIZADO"))
                {
                    runOnUiThread(() -> {
                        tvFechaEntrega.setText("Fecha de Entrega: " + registro.getFecha_entrega());
                        tvFechaEntrega.setVisibility(View.VISIBLE);
                    });
                }

                if(!registro.getNombre().equals(""))
                {
                    runOnUiThread(() -> {
                        etNombre.setText(registro.getNombre());
                    });
                }

                if(!registro.getTelefono().equals(""))
                {
                    runOnUiThread(() -> {
                        etTelefono.setText(registro.getTelefono());
                    });
                }

                runOnUiThread(() -> {
                    etCosto.setText(registro.getPrecio()+"$");
                });

                if(!registro.isPagado()){
                    runOnUiThread(() -> {
                        llSinPagar.setVisibility(View.VISIBLE);
                    });
                }
                else if(registro.getMetodo()==1){
                    runOnUiThread(() -> {
                        llEfectivo.setVisibility(View.VISIBLE);
                    });
                }
                else if(registro.getMetodo()==2){
                    runOnUiThread(() -> {
                        llTransferencia.setVisibility(View.VISIBLE);
                    });
                }

                else if(registro.getMetodo()==3){
                    runOnUiThread(() -> {
                        llCortesia.setVisibility(View.VISIBLE);
                    });
                }

                if(!registro.getObservaciones().equals(""))
                {

                    runOnUiThread(() -> {
                        llObservaciones.setVisibility(View.VISIBLE);
                        etObservaciones.setText(registro.getObservaciones());
                    });
                }


                if(Home.mUser.isAdministrador() && registro.getStatus().equals("EN_BODEGA") && registro.getPrecio()!=0 && !registro.getTelefono().equals("") && registro.isActivo())
                {
                    runOnUiThread(() -> {
                        btnChangeStatus.setVisibility(View.VISIBLE);
                    });
                }
                else if(Home.mUser.isAdministrador() && registro.getStatus().equals("EN_CAMINO"))
                    runOnUiThread(() -> {
                        btnFinalizar.setVisibility(View.VISIBLE);
                    });



                if(registro.getId_operador()!=0){
                    new Thread(() -> {
                        String operador= mSql.getOperador(registro.getId_operador());

                        runOnUiThread(() -> {
                            etNombreOperador.setText(operador);
                            cvOperador.setVisibility(View.VISIBLE);
                        });
                    }).start();
                }



                new Thread(() -> {
                    while(true)
                    {
                        if(menuLoad)
                        {
                            if(Home.mUser.isAdministrador() && (registro.getStatus().equals("EN_BODEGA") && (registro.getPrecio()==0 || registro.getTelefono().equals(""))))
                            {
                                runOnUiThread(() -> {
                                    getMenuInflater().inflate(R.menu.menu_toolbar_registros_detalles_incompleto, this.menu); //MOSTRAR
                                    menuLoad = false;
                                });
                            }
                            else if(Home.mUser.isAdministrador() && registro.getStatus().equals("EN_BODEGA") && registro.isActivo())
                            {
                                runOnUiThread(() -> {
                                    getMenuInflater().inflate(R.menu.menu_toolbar_registros_detalles, this.menu); //MOSTRAR
                                    menuLoad = false;
                                });
                            }
                            else {
                                    menuLoad = false;
                            }
                            break;
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                }).start();


                runOnUiThread(() -> {
                    linearlayoutLoad.setVisibility(View.GONE);
                    scrollview.setVisibility(View.VISIBLE);
                });
            }
            else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }


            new Thread(() -> {

                if(mSql.consultarPaquetes(codigo))
                {
                    mRegistrosPaquetes = mSql.getmRegistrosPaquetes();

                    runOnUiThread(() -> {
                        registrosPaqueteListAdapter = new RegistroPaquetesListAdapter(mRegistrosPaquetes, this);
                        mRecyclerPaquetes.setHasFixedSize(true);
                        mRecyclerPaquetes.setLayoutManager(new LinearLayoutManager(this));
                        mRecyclerPaquetes.setAdapter(registrosPaqueteListAdapter);
                    });
                }



            }).start();

            new Thread(() -> {
                if(mSql.consultarGuias(codigo))
                {
                    mRegistrosGuias = mSql.getmRegistrosGuias();

                    if(mRegistrosGuias.size()!=0)
                    {
                        runOnUiThread(() -> {
                            /*
                            registrosGuiaListAdapter = new RegistroGuiasListAdapter(mRegistrosGuias, this);
                            mRecyclerGuias.setHasFixedSize(true);
                            mRecyclerGuias.setLayoutManager(new LinearLayoutManager(this));
                            mRecyclerGuias.setAdapter(registrosGuiaListAdapter);
*/

                            mRegistroGuiasGridAdapter = new RegistroGuiasGridAdapter(HomeRegistrosDetalles.this, mRegistrosGuias);
                            gvGuias.setAdapter(mRegistroGuiasGridAdapter);


                        });


                    }
                    else {
                        runOnUiThread(() -> {
                            cvGuias.setVisibility(View.GONE);
                        });
                    }


                }

            }).start();
        }).start();




    }


    private void leerFotos() {
        new Thread(() -> {

            //ALGORITMO LEER DE MEMORIA O DE BASE DE DATOS - AMBAS



            new Thread(() -> {
                try
                {
                    registro.setFoto_recibido(getBitmapFromDir(codigo, "REC"));
                }
                catch(Exception e)
                {
                    if(mSql.downloadImagen(codigo))
                    {
                        registro.setFoto_recibido(mSql.getImagen());
                        new Thread(() -> {
                            guardarImagen(codigo, registro.getFoto_recibido());
                        }).start();
                    }
                    else
                    {
                        //BITMAP = IMAGEN DE FALLO;
                    }
                }

                runOnUiThread(() -> {
                    ivFotoBodega.setImageBitmap(registro.getFoto_recibido());
                    ivFotoBodega.setVisibility(View.VISIBLE);
                });



            }).start();







            


            if(registro.getStatus().equals("FINALIZADO"))
            {



                new Thread(() -> {
                    try
                    {
                        registro.setFoto_entrega(getBitmapFromDir(codigo, "ENT"));
                    }
                    catch(Exception e)
                    {
                        if(mSql.downloadImagenEntrega(codigo))
                        {
                            registro.setFoto_entrega(mSql.getImagen());
                            new Thread(() -> {
                                guardarImagen(codigo, registro.getFoto_entrega());
                            }).start();
                        }
                        else
                        {
                            //BITMAP = IMAGEN DE FALLO;
                        }
                    }

                    runOnUiThread(() -> {
                        ivFotoEntrega.setImageBitmap(registro.getFoto_entrega());
                        ivFotoEntrega.setVisibility(View.VISIBLE);
                    });



                }).start();


            }


        }).start();
    }

    private void guardarImagen(String codigo, Bitmap bitmap)
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
        } catch (Exception e){}
    }

    private String crearNombreArchivoJPG(String codigo)
    {
        return codigo +"_ENT" + ".jpg";
    }

    private Bitmap getBitmapFromDir(String codigo, String input) throws IOException {
        //FileInputStream read = ((Activity) context).openFileInput(codigo + "_REC" + ".jpg");

        String cadena = Environment.getExternalStorageDirectory().toString() + "/RhinoPacking" + "/" + codigo + "_" + input + ".jpg";
        FileInputStream read = new FileInputStream(cadena);

        int size = read.available();
        byte[] buffer = new byte[size];
        read.read(buffer);
        read.close();
        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bitmap;
    }

    private void leerStatus(){
        new Thread(() -> {


            if(!registro.isActivo())
            {
                runOnUiThread(() -> {
                    tvStatus.setText("Eliminado");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_incompleta, 0, 0, 0);
                });

            }
            else if(registro.getStatus().equals("FINALIZADO") && registro.getMetodo()!=0)
            {
                runOnUiThread(() -> {
                    tvStatus.setText("Finalizado");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_finalizado, 0, 0, 0);
                });

            }
            else if(registro.getStatus().equals("FINALIZADO") && registro.getMetodo()==0)
            {
                runOnUiThread(() -> {
                    tvStatus.setText("Finalizado\nDebe");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deuda, 0, 0, 0);
                });

            }

            else if(registro.getStatus().equals("EN_CAMINO") && registro.getMetodo()!=0)
            {
                runOnUiThread(() -> {
                    tvStatus.setText("En Camino");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camino, 0, 0, 0);
                });

            }
            else if(registro.getStatus().equals("EN_CAMINO") && registro.getMetodo()==0)
            {
                runOnUiThread(() -> {
                    tvStatus.setText("En Camino\nDebe");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deuda, 0, 0, 0);
                });
            }

            else if(registro.getStatus().equals("EN_BODEGA") && registro.getMetodo()!=0)
            {
                runOnUiThread(() -> {
                    tvStatus.setText("En Bodega");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bodega, 0, 0, 0);
                });

            }
            else if(registro.getStatus().equals("EN_BODEGA") && registro.getMetodo()==0)
            {
                runOnUiThread(() -> {
                    tvStatus.setText("En Bodega\nDebe");
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deuda, 0, 0, 0);
                });
            }
        }).start();
    }



    private void habilitar(boolean b)
    {
        if(b)
        {
            runOnUiThread(() -> {
                etNombre.setEnabled(true);
                etTelefono.setEnabled(true);
                etCosto.setEnabled(true);
                etObservaciones.setEnabled(true);
                etObservaciones.setVisibility(View.VISIBLE);
            });
        }
        else {
            runOnUiThread(() -> {
                etNombre.setEnabled(false);
                etTelefono.setEnabled(false);
                etCosto.setEnabled(false);
                etObservaciones.setEnabled(false);
            });

            /*
            if(etObservaciones.getText().toString().equals(""))
            {
                runOnUiThread(() -> {
                    etObservaciones.setVisibility(View.GONE);
                });
            }*/
        }
    }


    private void eliminar() {
        mPopup.setPopupEliminar(codigo);
        try
        {
            mPopup.setPopupEliminar(codigo);
        }
        catch (Exception ignored){}
    }

    Menu menu;
    boolean menuLoad = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        this.menu.clear();
        menuLoad = true;
        return true;
    }

    @SuppressLint({"RtlHardcoded", "NonConstantResourceId"})
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                backPress();
                break;

            case R.id.btnEliminar:
                if(pressButton)
                    return true;
                else
                {
                    pressButton = true;
                    eliminar();
                    pressButton = false;
                }
            break;

            case R.id.btnEditar:
                if(pressButton)
                    return true;
                else
                {
                    pressButton = true;
                    menu.clear();
                    getMenuInflater().inflate(R.menu.menu_toolbar_ok, this.menu); //MOSTRAR

                    habilitar(true);







                    pressButton = false;
                }
            break;


            case R.id.btnCancel:
                if(pressButton)
                    return true;
                else
                {
                    pressButton = true;
                    menu.clear();
                    getMenuInflater().inflate(R.menu.menu_toolbar_registros_detalles_incompleto, this.menu); //MOSTRAR
                    habilitar(false);
                    pressButton = false;
                }
            break;

            case R.id.btnOk:
                if(pressButton)
                    return true;
                else
                {
                    new Thread(() -> {
                        /////SQL UPDATE EDITAR
                        if(valEditar())
                        {



                            if(mSql.updateDatos(codigo, etNombre.getText().toString(), etTelefono.getText().toString(), etCosto.getText().toString().replace("$", ""), etObservaciones.getText().toString()))
                            {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                                    menu.clear();
                                    getMenuInflater().inflate(R.menu.menu_toolbar_registros_detalles_incompleto, this.menu); //MOSTRAR
                                });
                                habilitar(false);
                            }

                            pressButton = false;
                        }
                        else {
                            //getMenuInflater().inflate(R.menu.menu_toolbar_registros_detalles_incompleto, this.menu); //MOSTRAR
                            pressButton = false;
                        }

                    }).start();








                }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean valEditar(){

        if(etNombre.getText().toString().equals(""))
        {
            runOnUiThread(() -> {
                mPopupError.setPopupError("Debes ingresar Nombre.");
            });
            SleepButton();
        }
        if(etTelefono.getText().toString().equals(""))
        {
            runOnUiThread(() -> mPopupError.setPopupError("Debes ingresar el Teléfono."));
            SleepButton();
        }
        else if (etTelefono.length() !=0 && etTelefono.length() != 10)
        {

            runOnUiThread(() -> mPopupError.setPopupError("El número de teléfono debe ser de 10 dígitos."));
            SleepButton();
        }
        else if(etCosto.getText().toString().equals(""))
        {
            runOnUiThread(() -> mPopupError.setPopupError("Debes ingresar el Costo."));
            SleepButton();
        }
        else {
            return true;
        }


        return false;
    }



    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        mSql.close();
        finish();
    }
    //BACK PRESS
}