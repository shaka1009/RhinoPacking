package com.rhinopacking.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    @Override public void onBindViewHolder(ViewHolder holder, int position)
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
        }
        void bindData(Registro item)
        {
            SQL mSql = new SQL();
            String status = "";


            tvCodigo.setText(item.getCodigo());
            tvNombre.setText("Nombre: " + item.getNombre());
            tvSemana.setText("Semana: " +  item.getSemana());

            if(!item.isActivo())
            {
                tvStatus.setText("Eliminado");
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_incompleta, 0, 0, 0);
            }
            else if(item.getStatus().equals("FINALIZADO") && item.getMetodo()!=0)
            {
                status = "Finalizado";
                tvStatus.setText(status);
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_finalizado, 0, 0, 0);
            }
            else if(item.getStatus().equals("FINALIZADO") && item.getMetodo()==0)
            {
                status = "Finalizado\nDebe";
                tvStatus.setText(status);
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deuda, 0, 0, 0);
            }

            else if(item.getStatus().equals("EN_CAMINO") && item.getMetodo()!=0)
            {
                status = "En Camino";
                tvStatus.setText(status);
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camino, 0, 0, 0);
            }
            else if(item.getStatus().equals("EN_CAMINO") && item.getMetodo()==0)
            {
                status = "En Camino\nDebe";
                tvStatus.setText(status);
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deuda, 0, 0, 0);
            }

            else if(item.getStatus().equals("EN_BODEGA") && item.getMetodo()!=0)
            {
                status = "En Bodega";
                tvStatus.setText(status);
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bodega, 0, 0, 0);
            }
            else if(item.getStatus().equals("EN_BODEGA") && item.getMetodo()==0)
            {
                status = "En Bodega\nDebe";
                tvStatus.setText(status);
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deuda, 0, 0, 0);
            }








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

            File mainDir= new File(Environment.getExternalStorageDirectory().getPath(), "RhinoPacking");
            //File mainDir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"", "RhinoPacking");
            if (!mainDir.exists()) {
                if(mainDir.mkdirs())
                {
                    Log.d("Shaka", "Dir creado:"+ mainDir);
                }

                else
                {
                    Log.d("Shaka", "Dir No creado:"+ mainDir);
                }
            }
            else
            {
                Log.d("Shaka", "existe" + mainDir);

                String imageName = crearNombreArchivoJPG(codigo);
                File file = new File(mainDir, imageName);
                OutputStream out;
                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                }catch (Exception e)
                {
                    Log.d("SHAKA", "Error: " + e);
                }
            }
        } catch (Exception e){}
    }

    private String crearNombreArchivoJPG(String codigo)
    {
        return codigo +"_REC" + ".jpg";
    }

    private Bitmap getBitmapFromDir(String codigo) throws IOException {
        //FileInputStream read = ((Activity) context).openFileInput(codigo + "_REC" + ".jpg");

        String cadena = Environment.getExternalStorageDirectory().toString() + "/RhinoPacking" + "/" + codigo + "_REC" + ".jpg";
        FileInputStream read = new FileInputStream(cadena);

        int size = read.available();
        byte[] buffer = new byte[size];
        read.read(buffer);
        read.close();
        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bitmap;
    }


}
