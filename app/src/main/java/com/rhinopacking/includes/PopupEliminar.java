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


public class PopupEliminar {

    Context context;
    Context contextApp;
    View error;
    boolean pressButton;

    private Dialog popupEliminar;
    public Button popupEliminarNo, popupEliminarSi;
    private TextView tvPopupEliminar;

    public PopupEliminar(Context context, Context contextApp, View error) {
        this.context = context;
        this.contextApp = contextApp;
        this.error = error;
        pressButton = false;
        declaration();
    }

    private void declaration() {
        ///popup cerrar sesion
        popupEliminar = new Dialog(context);
        popupEliminar.setContentView(R.layout.popup_eliminar);
        popupEliminar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupEliminarSi = popupEliminar.findViewById(R.id.popupSi);
        popupEliminarNo = popupEliminar.findViewById(R.id.popupNo);
        tvPopupEliminar = popupEliminar.findViewById(R.id.tvEliminar);
    }


    @SuppressLint("SetTextI18n")
    public void setPopupEliminar(String codigo)
    {
        tvPopupEliminar.setText("¿Deseas eliminar el registro con el código: " + codigo + "?");

        popupEliminarNo.setOnClickListener(v -> popupEliminar.dismiss());

        popupEliminar.show();
    }

    public void hidePopupCerrarSesion()
    {
        popupEliminar.dismiss();
    }
}
