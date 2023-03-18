package com.rhinopacking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.rhinopacking.models.RegistroGuia;

import java.util.ArrayList;
import java.util.List;

public class RegistroGuiasGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<RegistroGuia> mRegistrosGuias;

    public RegistroGuiasGridAdapter(Context mContext, List<RegistroGuia> mRegistrosGuias) {
        this.mContext = mContext;
        this.mRegistrosGuias = mRegistrosGuias;
    }


    @Override
    public int getCount() {
        return mRegistrosGuias.size();
    }

    @Override
    public Object getItem(int i) {
        return mRegistrosGuias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(mRegistrosGuias.get(i).getFoto());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                340, 350
        ));
        return imageView;
    }
}
