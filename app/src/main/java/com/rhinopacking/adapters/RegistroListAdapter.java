package com.rhinopacking.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rhinopacking.DB.SQL;
import com.rhinopacking.R;
import com.rhinopacking.models.Registro;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;




public class RegistroListAdapter extends RecyclerView.Adapter<RegistroListAdapter.ViewHolder> {

    private List<Registro> mData;
    private LayoutInflater mInflater;
    private Context context;

    public RegistroListAdapter(List<Registro> itemList, Context context)
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
        View view = mInflater.inflate(R.layout.rv_registros, null);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Registro> items)
    {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvStatus, tvCodigo, tvNombre, tvSemana;
        ImageView fotoAlmacen;
        Bitmap bitmap;
        ProgressBar progressBar;

        TextView tvAlertaFinalizado, tvAlertaCamino, tvAlertaBodega, tvAlertaDeuda, tvAlertaIncompleta;

        ViewHolder(View itemView)
        {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvCodigo = itemView.findViewById(R.id.tvCodigo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvSemana = itemView.findViewById(R.id.tvSemana);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            fotoAlmacen = itemView.findViewById(R.id.fotoRecibido);
            tvAlertaFinalizado = itemView.findViewById(R.id.tvAlertaFinalizado);
            tvAlertaCamino = itemView.findViewById(R.id.tvAlertaCamino);
            tvAlertaBodega = itemView.findViewById(R.id.tvAlertaBodega);
            tvAlertaDeuda = itemView.findViewById(R.id.tvAlertaDeuda);
            tvAlertaIncompleta = itemView.findViewById(R.id.tvAlertaIncompleta);
        }
        void bindData(final Registro item)
        {
            SQL mSql = new SQL();
            tvCodigo.setText(item.getCodigo());
            tvNombre.setText("Nombre: " + item.getNombre());
            tvSemana.setText("Semana: " +  item.getSemana());

            new Thread(() -> {

                String status = "";

                if(item.getStatus().equals("FINALIZADO"))
                {
                    status = "Finalizado";
                    if(item.isPagado()){
                        ((Activity) context).runOnUiThread(() -> {
                            tvAlertaFinalizado.setVisibility(View.VISIBLE);
                        });
                    }
                }
                else if(item.getStatus().equals("EN_CAMINO"))
                {
                    status = "En Camino";
                    if(item.isPagado()){

                        ((Activity) context).runOnUiThread(() -> {
                            tvAlertaCamino.setVisibility(View.VISIBLE);
                        });
                    }
                }
                else if(item.getStatus().equals("EN_BODEGA"))
                {
                    status = "En Bodega";

                    if(item.getPrecio()==0 || item.getTelefono().equals(""))
                    {
                        status = status + "\nIncompleto";
                        ((Activity) context).runOnUiThread(() -> {

                            tvAlertaIncompleta.setVisibility(View.VISIBLE);
                        });

                    }
                    else if(item.isPagado())
                    {
                        ((Activity) context).runOnUiThread(() -> {
                            tvAlertaBodega.setVisibility(View.VISIBLE);
                        });
                    }
                }




                if(!item.isPagado() && item.getPrecio()!=0 && !item.getTelefono().equals("")){
                    status = status + "\nDebe";
                    ((Activity) context).runOnUiThread(() -> {

                        tvAlertaDeuda.setVisibility(View.VISIBLE);
                    });
                }

                String finalStatus = status;
                ((Activity) context).runOnUiThread(() -> {
                    tvStatus.setText(finalStatus);
                });
            }).start();


            new Thread(() -> {
                try
                {
                    bitmap = getBitmapFromDir(item.getCodigo());
                }
                catch(Exception e)
                {
                    if(mSql.downloadImagen(item.getCodigo()))
                    {
                        bitmap = mSql.getImagen();
                        new Thread(() -> {
                            guardarImagen(item.getCodigo(), bitmap);
                        }).start();
                    }
                    else
                    {
                        //BITMAP = IMAGEN DE FALLO;
                    }
                }

                ((Activity) context).runOnUiThread(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    fotoAlmacen.setVisibility(View.VISIBLE);
                    fotoAlmacen.setImageBitmap(bitmap);
                });
            }).start();
        }
    }

    private void guardarImagen(String codigo, Bitmap bitmap)
    {
        try {
            FileOutputStream fos = ((Activity) context).openFileOutput(crearNombreArchivoJPG(codigo), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

        } catch (Exception e){}
    }

    private String crearNombreArchivoJPG(String codigo)
    {
        return "REC" + "_" + codigo;
    }

    private Bitmap getBitmapFromDir(String codigo) throws IOException {
        FileInputStream read = ((Activity) context).openFileInput("REC" + "_" + codigo);
        int size = read.available();
        byte[] buffer = new byte[size];
        read.read(buffer);
        read.close();
        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bitmap;
    }


}
