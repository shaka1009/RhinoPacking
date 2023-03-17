package com.rhinopacking.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rhinopacking.R;
import com.rhinopacking.models.RegistroGuia;
import com.rhinopacking.models.RegistroPaquete;

import java.util.List;


public class RegistroGuiasListAdapter extends RecyclerView.Adapter<RegistroGuiasListAdapter.ViewHolder> {

    private List<RegistroGuia> mData;
    private OnItemClickListener mListener;
    private LayoutInflater mInflater;
    private Context context;


    public interface OnItemClickListener {
        //void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RegistroGuiasListAdapter(List<RegistroGuia> itemList, Context context)
    {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.rv_registros_guias, null);
        return new ViewHolder(view, mListener);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<RegistroGuia> items)
    {
        mData = items;
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPaquete, tvCantidad, tvMedidas, tvEliminar;
        ImageView fotoPaquete;

        ViewHolder(View itemView, final OnItemClickListener listener)
        {
            super(itemView);

            fotoPaquete = itemView.findViewById(R.id.fotoPaquete);
            tvEliminar = itemView.findViewById(R.id.tvEliminar);

            tvEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

        }
        void bindData(final RegistroGuia item)
        {
            fotoPaquete.setImageBitmap(item.getFoto());

            if(item.getId()!=0)
                tvEliminar.setVisibility(View.INVISIBLE);
        }
    }



}