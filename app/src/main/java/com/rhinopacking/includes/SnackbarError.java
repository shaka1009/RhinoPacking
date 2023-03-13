package com.rhinopacking.includes;

import android.content.Context;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.rhinopacking.R;

public class SnackbarError {

    private CoordinatorLayout snackbar;
    private Context context;
    private Snackbar snackbar1, snackbar2;

    public SnackbarError(CoordinatorLayout snackbar, Context context)
    {
        this.snackbar = snackbar;
        this.context = context;

        snackbar1 = Snackbar.make(this.snackbar, "", Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar1.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.rojoAlert));

        snackbar2 = Snackbar.make(this.snackbar, "", Snackbar.LENGTH_INDEFINITE);
        View snackBarView2 = snackbar2.getView();
        snackBarView2.setBackgroundColor(ContextCompat.getColor(context, R.color.rojoAlert));
    }

    public void show(String error)
    {
        snackbar1.setText(error);
        snackbar1.show();
    }

    public void showInd(String error)
    {
        snackbar2.setText(error);
        snackbar2.show();
    }

    public void dismiss()
    {
        snackbar2.dismiss();
    }
}
