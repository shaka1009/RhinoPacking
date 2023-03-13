package com.rhinopacking.includes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.rhinopacking.R;


public class PopupMostrarFoto {

    Context context;
    Context contextApp;
    View error;
    boolean pressButton;

    private Dialog popupMostrarFoto;

    ImageView foto;


    public PopupMostrarFoto(Context context, Context contextApp, View error) {
        this.context = context;
        this.contextApp = contextApp;
        this.error = error;
        pressButton = false;
        declaration();
    }

    private void declaration() {
        ///popup cerrar sesion
        popupMostrarFoto = new Dialog(context);
        popupMostrarFoto.setContentView(R.layout.popup_mostrar_foto);
        popupMostrarFoto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        foto = popupMostrarFoto.findViewById(R.id.foto);
    }


    @SuppressLint("SetTextI18n")
    public void setPopupFoto(Bitmap bitmap)
    {
        foto.setImageBitmap(bitmap);
        popupMostrarFoto.show();
    }
}
