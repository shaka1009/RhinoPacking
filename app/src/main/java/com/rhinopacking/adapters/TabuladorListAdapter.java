package com.rhinopacking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rhinopacking.R;
import com.rhinopacking.models.Tabulador;

import java.util.List;


public class TabuladorListAdapter extends RecyclerView.Adapter<TabuladorListAdapter.ViewHolder> {

    private List<Tabulador> mData;
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

    public TabuladorListAdapter(List<Tabulador> itemList, Context context)
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
        View view = mInflater.inflate(R.layout.rv_tabulador, null);
        return new ViewHolder(view, mListener);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Tabulador> items)
    {
        mData = items;
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvN1, tvN2, tvN3, tvRes;
        ImageButton ibDelete;

        ViewHolder(View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            tvN1 = itemView.findViewById(R.id.tvN1);
            tvN2 = itemView.findViewById(R.id.tvN2);
            tvN3 = itemView.findViewById(R.id.tvN3);
            tvRes = itemView.findViewById(R.id.tvRes);
            ibDelete = itemView.findViewById(R.id.ibDelete);


            ibDelete.setOnClickListener(new View.OnClickListener() {
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
        void bindData(final Tabulador item)
        {
            tvN1.setText(String.valueOf(item.getN1()));
            tvN2.setText(String.valueOf(item.getN2()));
            tvN3.setText(String.valueOf(item.getN3()));
            tvRes.setText(String.valueOf(item.getR()));
        }
    }



}
