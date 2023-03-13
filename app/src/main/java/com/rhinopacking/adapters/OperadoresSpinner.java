package com.rhinopacking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rhinopacking.R;
import com.rhinopacking.models.Operador;

import java.util.ArrayList;



public class OperadoresSpinner extends ArrayAdapter<Operador> {
    public OperadoresSpinner(Context context, ArrayList<Operador> countryList) {
        super(context, 0, countryList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_operadores, parent, false
            );
        }
        ImageView imageViewFlag = convertView.findViewById(R.id.image_view_flag);
        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        Operador currentItem = getItem(position);
        if (currentItem != null) {
            //imageViewFlag.setImageResource(currentItem.getmFlagImage());
            textViewName.setText(currentItem.getNombre());
        }
        return convertView;
    }
}