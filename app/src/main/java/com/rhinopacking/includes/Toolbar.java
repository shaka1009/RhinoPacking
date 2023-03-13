package com.rhinopacking.includes;

import androidx.appcompat.app.AppCompatActivity;

import com.rhinopacking.R;


public class Toolbar {
    public static void showHome(AppCompatActivity activity, boolean upButton) {
        androidx.appcompat.widget.Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }


    public static void show(AppCompatActivity activity, boolean upButton) {
        androidx.appcompat.widget.Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    public static void show(AppCompatActivity activity, boolean upButton, String title) {
        androidx.appcompat.widget.Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
