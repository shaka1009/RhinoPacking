package com.rhinopacking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.rhinopacking.includes.Toolbar;

public class HomeMostrar extends AppCompatActivity {

    Bitmap bitmap;

    ImageView ivFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mostrar);
        Toolbar.show(this, true);

        ivFoto = findViewById(R.id.ivFoto);


        byte[] bytes = getIntent().getByteArrayExtra("img");
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        ivFoto.setImageBitmap(bitmap);
    }








    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
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