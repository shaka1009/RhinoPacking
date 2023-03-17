package com.rhinopacking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.rhinopacking.DB.SQL;
import com.rhinopacking.adapters.RegistroListAdapter;
import com.rhinopacking.includes.SnackbarError;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Registro;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeRegistros extends AppCompatActivity {

    boolean isLoaded;

    private boolean pressButton = false;

    private List<Registro> mRegistros;
    List<Registro> mRegistrosTemp;
    private RecyclerView mRecycler;

    private CoordinatorLayout snackbar;
    SnackbarError snackbarError;


    ProgressBar progressBar;
    TextView tvProgressBar;

    EditText etBuscador;
    Button btnBuscador;
    ImageButton btnQR;


    SQL mSql;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_registros);
        Toolbar.show(this, true, "Registros");

        declaration();
        listenner();


        loadDatos();

    }



    private void declaration() {
        isLoaded = false;
        mSql = new SQL();
        mRegistros = new ArrayList<>();
        mRecycler = findViewById(R.id.rvRegistros);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);

        snackbar = findViewById(R.id.snackbar_layout);
        snackbarError = new SnackbarError(snackbar, this);

        etBuscador = findViewById(R.id.etBuscador);
        btnBuscador = findViewById(R.id.btnBuscador);
        btnQR = findViewById(R.id.btnQR);

    }

    private void listenner() {
        new Thread(() -> {
            if(Home.mUser.isAdministrador())
            {
                runOnUiThread(() -> {
                    //faAdd.setVisibility(View.VISIBLE);
                });
            }

        }).start();

        btnQR.setOnClickListener(view -> {
            new Thread(() -> {
                if(pressButton)
                    return;
                else pressButton = true;

                if(isConnected())
                {
                    scanCode();
                }

                else {
                    runOnUiThread(() -> {
                        snackbarError.show("No hay conexión a internet.");
                    });
                }
                SleepButton();
            }).start();
        });

/*
        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               mRegistrosTemp = new ArrayList<>();

                for (int x=0;x<mRegistros.size();x++) {

                    if(  mRegistros.get(x).getCodigo().contains(etBuscador.getText().toString().toUpperCase()) || mRegistros.get(x).getNombre().toLowerCase().contains(etBuscador.getText().toString().toLowerCase()))
                        mRegistrosTemp.add(mRegistros.get(x));
                }
                mostrarAdapter(mRegistrosTemp);

            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });


 */
        btnBuscador.setOnClickListener(view -> {
            new Thread(() -> {
                if(pressButton)
                    return;
                else pressButton = true;

                if(isConnected())
                {
                    mRegistrosTemp = new ArrayList<>();

                    if(etBuscador.getText().toString().equals(""))
                        mostrarAdapter(mRegistros);
                    else {
                        for (int x=0;x<mRegistros.size();x++) {

                            if(mRegistros.get(x).getCodigo().contains(etBuscador.getText().toString().toUpperCase())
                                    || mRegistros.get(x).getNombre().toLowerCase().contains(etBuscador.getText().toString().toLowerCase())
                                    || mRegistros.get(x).getSemana().equals(etBuscador.getText().toString()))
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }
                }

                else {
                    runOnUiThread(() -> {
                        snackbarError.show("No hay conexión a internet.");
                    });
                }
                SleepButton();
            }).start();
        });




        mRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            final GestureDetector mGestureDetector = new GestureDetector(HomeRegistros.this, new GestureDetector.SimpleOnGestureListener() {
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

                        Intent intent = new Intent(HomeRegistros.this, HomeRegistrosDetalles.class);
                        intent.putExtra("codigo", mRegistrosTemp.get(position).getCodigo());
                        someActivityResultLauncher.launch(intent);

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
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    isLoaded=false;

                    loadDatos();

                }
            });


    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Prender flash: Volúmen arriba. \nApagar flash: Volúmen abajo.");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }


    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            if(!isLoaded)
            {
                Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                return;
            }


            etBuscador.setText(result.getContents());

            mRegistrosTemp = new ArrayList<>();

            for (int i=0;i<mRegistros.size();i++) {

                if(mRegistros.get(i).getCodigo().equals(result.getContents()))
                    mRegistrosTemp.add(mRegistros.get(i));
            }

            mostrarAdapter(mRegistrosTemp);
        }
    });

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

    boolean valConnect=false;
    private void loadDatos()
    {
        new Thread(() -> {
            while (true)
            {
                if(isConnected())
                {
                    if(valConnect)
                    {
                        runOnUiThread(() -> {
                            snackbarError.dismiss();
                        });
                        valConnect = false;
                    }


                    sqlDatos();
                    break;
                }
                else if(!valConnect){
                    valConnect = true;
                    runOnUiThread(() -> {
                        snackbarError.showInd("No hay conexión a internet.");
                    });
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }).start();

    }



    private void sqlDatos() {

        new Thread(() -> {
            if(mSql.consultarRegistros()){
                mRegistros = mSql.getRegistros();
                mRegistrosTemp = new ArrayList<>();

                for (int x=0;x<mRegistros.size();x++)
                {
                    if(mRegistros.get(x).isActivo())
                        mRegistrosTemp.add(mRegistros.get(x));
                }


                isLoaded = true;
                mostrarAdapter(mRegistrosTemp);
            }
            else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();
                });
                finish();
            }


        }).start();
    }

    private void mostrarAdapter(List<Registro> mRegistros) {
        loadingVisible(false);
        RegistroListAdapter registrosListAdapter = new RegistroListAdapter(mRegistros, this);
        runOnUiThread(() -> {
            mRecycler.setAdapter(registrosListAdapter);
        });

    }


    private void loadingVisible(boolean mostrar) {
        if(mostrar)
        {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                tvProgressBar.setVisibility(View.VISIBLE);
            });

        }
        else {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                tvProgressBar.setVisibility(View.INVISIBLE);
            });
        }
    }


    private void mostrarLoading(String msg)
    {
        runOnUiThread(() -> {
            tvProgressBar.setText(msg);
        });
        loadingVisible(true);
    }


    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }





    Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if(Home.mUser.isAdministrador())
            getMenuInflater().inflate(R.menu.menu_toolbar_registros, this.menu); //MOSTRAR
        else
            getMenuInflater().inflate(R.menu.menu_toolbar_registros_operador, this.menu); //MOSTRAR
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                backPress();
            break;

            case R.id.menu_todos:

                if(pressButton)
                    return true;
                else
                {

                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    pressButton = true;

                    if(isConnected())
                    {

                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++)
                        {
                            if(mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }

                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }

                    SleepButton();
                }


            break;

            case R.id.menu_finalizados:
                if(pressButton)
                    return true;
                else
                {
                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    pressButton = true;

                    if(isConnected())
                    {
                         mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(mRegistros.get(x).getStatus().equals("FINALIZADO") && mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }

                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }

                    SleepButton();
                }
            break;

            case R.id.menu_en_camino:
                if(pressButton)
                    return true;
                else
                {
                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    pressButton = true;

                    if(isConnected())
                    {
                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(mRegistros.get(x).getStatus().equals("EN_CAMINO") && mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }

                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }

                    SleepButton();
                }
            break;

            case R.id.menu_en_bodega:
                if(pressButton)
                    return true;
                else
                {
                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    pressButton = true;

                    if(isConnected())
                    {
                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(mRegistros.get(x).getStatus().equals("EN_BODEGA")  && mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }

                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }

                    SleepButton();
                }
                break;


            case R.id.menu_deuda:
                if(pressButton)
                    return true;
                else
                {
                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    pressButton = true;

                    if(isConnected())
                    {
                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(!mRegistros.get(x).isPagado()  && mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }

                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }

                    SleepButton();
                }
                break;

            case R.id.menu_incompletos:
                if(pressButton)
                    return true;
                else
                {
                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    pressButton = true;

                    if(isConnected())
                    {
                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(mRegistros.get(x).isActivo() && mRegistros.get(x).getPrecio()==0 && mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }
                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }



                    SleepButton();
                }
                break;





            case R.id.menu_activos:
                if(pressButton)
                    return true;
                else
                {
                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    pressButton = true;



                    if(isConnected())
                    {
                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }


                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }



                    SleepButton();
                }
                break;

            case R.id.btnAdd:
                new Thread(() -> {
                    if(pressButton)
                        return;
                    else pressButton = true;

                    if(isConnected())
                    {

                        Intent intent = new Intent(this, HomeRegistrosAgregar.class);
                        someActivityResultLauncher.launch(intent);
                    }
                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }


                    SleepButton();
                }).start();
            break;

            case R.id.menu_eliminados:
                if(pressButton)
                    return true;
                else
                {

                    if(!isLoaded)
                    {
                        Toast.makeText(this, "No han cargado los datos.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    pressButton = true;



                    if(isConnected())
                    {
                        mRegistrosTemp = new ArrayList<>();

                        for (int x=0;x<mRegistros.size();x++) {
                            if(!mRegistros.get(x).isActivo())
                                mRegistrosTemp.add(mRegistros.get(x));
                        }
                        mostrarAdapter(mRegistrosTemp);
                    }


                    else {
                        runOnUiThread(() -> {
                            snackbarError.show("No hay conexión a internet.");
                        });
                    }



                    SleepButton();
                }
                break;

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