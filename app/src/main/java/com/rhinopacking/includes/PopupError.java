package com.rhinopacking.includes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rhinopacking.R;




public class PopupError {

    Context context;
    Context contextApp;
    View error;

    boolean pressButton;
    private BottomSheetDialog popupError;


    public PopupError(Context context, Context contextApp, View error) {
        this.context = context;
        this.contextApp = contextApp;
        this.error = error;
        pressButton = false;
        declaration();
    }

    private void declaration()
    {
        //POPUP ERROR
        popupError = new BottomSheetDialog(context, R.style.Theme_Design_BottomSheetDialog);
        View popupErrorView = LayoutInflater.from(contextApp).inflate(R.layout.popup_error, (ViewGroup) error);
        popupError.setContentView(popupErrorView);
        btnPoupErrorAceptar = popupError.findViewById(R.id.btnPoupErrorAceptar);
        assert btnPoupErrorAceptar != null;
        btnPoupErrorAceptar.setOnClickListener(v ->popupError.dismiss());
        //tvPoupError = popupError.findViewById(R.id.tvPoupError);




    }

    //POPUP ERROR

    public Button btnPoupErrorAceptar;
    @SuppressLint("SetTextI18n")
    public void setPopupError(String msg)
    {
        btnPoupErrorAceptar.setVisibility(View.VISIBLE);
        btnPoupErrorAceptar.setText("Aceptar");
        TextView tvErrorPopup = popupError.findViewById(R.id.tvPoupError);
        assert tvErrorPopup != null;
        tvErrorPopup.setText(msg);
        popupError.show();
    }


}
