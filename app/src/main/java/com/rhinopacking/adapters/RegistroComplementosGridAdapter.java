package com.rhinopacking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rhinopacking.models.RegistroComplemento;
import com.rhinopacking.models.RegistroGuia;

import java.util.List;

public class RegistroComplementosGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<RegistroComplemento> mRegistrosComplementos;

    public RegistroComplementosGridAdapter(Context mContext, List<RegistroComplemento> mRegistrosComplementos) {
        this.mContext = mContext;
        this.mRegistrosComplementos = mRegistrosComplementos;
    }


    @Override
    public int getCount() {
        return mRegistrosComplementos.size();
    }

    @Override
    public Object getItem(int i) {
        return mRegistrosComplementos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(mRegistrosComplementos.get(i).getFoto());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                340, 350
        ));
        return imageView;
    }
}
