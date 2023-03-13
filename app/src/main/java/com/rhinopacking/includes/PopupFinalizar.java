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


public class PopupFinalizar {

    Context context;
    Context contextApp;
    View error;
    boolean pressButton;

    private Dialog popupFinalizar;
    public Button popupFinalizarBtn, popupFinalizarCancelar;
    private TextView tvFinalizar;

    public PopupFinalizar(Context context, Context contextApp, View error) {
        this.context = context;
        this.contextApp = contextApp;
        this.error = error;
        pressButton = false;
        declaration();
    }

    private void declaration() {
        ///popup cerrar sesion
        popupFinalizar = new Dialog(context);
        popupFinalizar.setContentView(R.layout.popup_finalizar);
        popupFinalizar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupFinalizarBtn = popupFinalizar.findViewById(R.id.popupFinalizar);
        popupFinalizarCancelar = popupFinalizar.findViewById(R.id.popupCancelar);
        tvFinalizar = popupFinalizar.findViewById(R.id.tvFinalizar);
    }


    @SuppressLint("SetTextI18n")
    public void setPopup(String nombre)
    {

        tvFinalizar.setText(nombre);


        popupFinalizarCancelar.setOnClickListener(v -> popupFinalizar.dismiss());

        popupFinalizar.show();
    }

    public void hidePopupCerrarSesion()
    {
        popupFinalizar.dismiss();
    }
}
