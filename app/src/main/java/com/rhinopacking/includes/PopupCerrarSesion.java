package com.rhinopacking.includes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rhinopacking.R;


public class PopupCerrarSesion {

    Context context;
    Context contextApp;
    View error;
    boolean pressButton;

    private Dialog popupCerrarSesion;
    public Button popupCerrarSesionCancelar, popupCerrarSesionSalir;
    private TextView tvPopupCerrarSesion;

    public PopupCerrarSesion(Context context, Context contextApp, View error) {
        this.context = context;
        this.contextApp = contextApp;
        this.error = error;
        pressButton = false;
        declaration();
    }

    private void declaration() {
        ///popup cerrar sesion
        popupCerrarSesion = new Dialog(context);
        popupCerrarSesion.setContentView(R.layout.popup_cerrar_sesion);
        popupCerrarSesion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupCerrarSesionCancelar = popupCerrarSesion.findViewById(R.id.popupCerrarSesionCancelar);
        popupCerrarSesionSalir = popupCerrarSesion.findViewById(R.id.popupCerrarSesionSalir);
        tvPopupCerrarSesion = popupCerrarSesion.findViewById(R.id.tvCerrarSesion);
    }


    @SuppressLint("SetTextI18n")
    public void setPopupCerrarSesion(String nombre)
    {

        tvPopupCerrarSesion.setText(nombre + ", estás a punto de cerrar la sesión en la aplicación de Rhino Paking.");


        popupCerrarSesionCancelar.setOnClickListener(v -> popupCerrarSesion.dismiss());

        popupCerrarSesion.show();
    }

    public void hidePopupCerrarSesion()
    {
        popupCerrarSesion.dismiss();
    }
}
