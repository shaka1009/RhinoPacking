package com.rhinopacking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.rhinopacking.DB.SQL;
import com.rhinopacking.includes.PopupCerrarSesion;
import com.rhinopacking.includes.PopupError;
import com.rhinopacking.includes.SnackbarError;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.User;
import com.rhinopacking.providers.AuthProvider;
import com.rhinopacking.providers.UserProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.StringTokenizer;

public class Home extends AppCompatActivity {

    static final int RESOLUCION = 720;

    private DrawerLayout drawer;
    NavigationView navigationView;

    private CoordinatorLayout snackbar;
    SnackbarError snackbarError;
    private PopupError mPopupError;
    private PopupCerrarSesion mPopupCerrarSesion;
    private TextView tvEmpleado, tvNombre, tvTelefono, tvSaludo;

    public static User mUser;
    private AuthProvider mAuthProvider;
    private UserProvider mUserProvider;

    private boolean pressButton;

    CardView cvRegistros, cvFinalizar, cvTabulador;

    private ConnectivityManager cm;

    SQL mSql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar.showHome(this, true);

        declaration();
        listenner();
        listenner_drawer();
        cargarDatos();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    @SuppressLint("CutPasteId")
    private void declaration() {

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        mSql = new SQL();

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //REVISAR PERMISOS
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String []{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        requestPermissions(PERMISSIONS_STORAGE,30);

        mPopupCerrarSesion = new PopupCerrarSesion(this, getApplicationContext(), findViewById(R.id.popupError));

        //MENÚ DESLIZANTE
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        tvEmpleado = hView.findViewById(R.id.slide_empleado);
        tvNombre = hView.findViewById(R.id.slide_nombre);
        tvTelefono = hView.findViewById(R.id.slide_telefono);
        tvSaludo = findViewById(R.id.tvSaludo);

        //barras de error
        mPopupError = new PopupError(this, getApplicationContext(), findViewById(R.id.popupError));
        snackbar = findViewById(R.id.snackbar_layout);
        snackbarError = new SnackbarError(snackbar, this);


        cvRegistros = findViewById(R.id.cvRegistros);
        cvFinalizar = findViewById(R.id.cvFinalizar);
        cvTabulador = findViewById(R.id.cvTabulador);
    }


    private void listenner(){
        pressButton = false;

        mPopupCerrarSesion.popupCerrarSesionSalir.setOnClickListener(v -> new Thread(() -> {
            mPopupCerrarSesion.hidePopupCerrarSesion();
            if(pressButton)
                return;
            else pressButton = true;


            try{
                deleteFile("Acc_App");
            }catch(Exception ignored){}


            mAuthProvider.logout();
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            SleepButton();
            finish();
        }).start());



        cvRegistros.setOnClickListener(view -> {
            new Thread(() -> {
                if(pressButton)
                    return;
                else pressButton = true;

                if(isConnected())
                    startActivity(new Intent(Home.this , HomeRegistros.class));
                else {
                    runOnUiThread(() -> {
                        snackbarError.show("No hay conexión a internet.");
                    });
                }
                SleepButton();
            }).start();
        });



        cvFinalizar.setOnClickListener(view -> {
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
                    SleepButton();
                }
            }).start();
        });

        cvTabulador.setOnClickListener(view -> {
            new Thread(() -> {
                if(pressButton)
                    return;
                else pressButton = true;

                startActivity(new Intent(Home.this , HomeTabulador.class));
                SleepButton();

            }).start();
        });
    }



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
            //Toast.makeText(this, "Código Escaneado:" + result.getContents(), Toast.LENGTH_SHORT).show();
            checkCodigo(result.getContents());
        }
        else {
            //Toast.makeText(this, "Scan:" + result.getContents(), Toast.LENGTH_SHORT).show();
            SleepButton();
        }
    });

    private void checkCodigo(String contents){
        new Thread(() -> {
            if(mSql.verificarEnCamino(contents))
            {
                Intent intent = new Intent(Home.this, HomeFinalizar.class);
                intent.putExtra("codigo", contents);
                startActivity(intent);
            }
            else {
                if(mSql.isFinalizado())
                {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Servicio ya finalizado.", Toast.LENGTH_SHORT).show();
                    });
                }

                else
                {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No hay conexión.", Toast.LENGTH_SHORT).show();
                    });
                }

            }
            SleepButton();
        }).start();
    }




    private void cargarDatos() {

        new Thread(() ->
        {
            if(!existDatos())
            {
                runOnUiThread(() ->
                {
                    mUserProvider.getDb().collection("usuarios").document(mAuthProvider.getTelefono()).get().addOnSuccessListener(documentSnapshot -> {
                        mUser = new User(Objects.requireNonNull(documentSnapshot.get("nombre")).toString(), mAuthProvider.getTelefono().toString(), Boolean.TRUE.equals(documentSnapshot.getBoolean("administrador")));
                        guardar_datos();
                    }).addOnFailureListener(e -> runOnUiThread(() -> {
                        Toast.makeText(this, "No hay conexión a internet.", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
                });
            }

        }).start();
    }

    private boolean existDatos() {
        try {
            FileInputStream read = openFileInput("Acc_App");
            int size = read.available();
            byte[] buffer = new byte[size];
            read.read(buffer);
            read.close();
            String text = new String(buffer);
            StringTokenizer token = new StringTokenizer(text, "\n");


            mUser = new User(token.nextToken(), token.nextToken(), token.nextToken());


            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {

                    tvNombre.setText(mUser.getNombre());
                    tvTelefono.setText(mUser.getTelefono());
                    if(mUser.isAdministrador())
                        tvEmpleado.setText("Administrador");
                    else
                        tvEmpleado.setText("Operador");
                    tvSaludo.setText(mUser.getNombre());
                }
            });



            return true;
        } catch (Exception e) {
            return false;
        }

    }


    private void guardar_datos()
    {
        try{
            //DELETE FILE
            try{
                deleteFile("Acc_App");
            }catch(Exception ignored){}
            //

            FileOutputStream conf = openFileOutput("Acc_App", Context.MODE_PRIVATE);
            String cadena =
                            mUser.getNombre() + "\n" +
                            mUser.getTelefono() + "\n" +
                            mUser.isAdministrador();

            tvNombre.setText(mUser.getNombre());
            tvTelefono.setText(mUser.getTelefono());
            if(mUser.isAdministrador())
                tvEmpleado.setText("Administrador");
            else
                tvEmpleado.setText("Operador");

            tvSaludo.setText(mUser.getNombre());

            conf.write(cadena.getBytes());
            conf.close();
        }
        catch(Exception ignored){}
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







    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }






    @SuppressLint("NonConstantResourceId")
    private void listenner_drawer() {
        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.menu_perfil:
                    if(pressButton)
                        break;
                    else pressButton = true;

                    SleepButton();
                    break;









                case R.id.acerca_de_app:
                    if(pressButton)
                        break;
                    else pressButton = true;

                    startActivity(new Intent(Home.this, HomeAcerca.class));

                    SleepButton();
                    break;

                case R.id.cerrar_sesion:
                    if(pressButton)
                        break;
                    else pressButton = true;

                    mPopupCerrarSesion.setPopupCerrarSesion(mUser.getNombre());

                    SleepButton();
                    break;
            }
            drawer.closeDrawers();
            return false;
        });
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true); //Minimizar
    }
}