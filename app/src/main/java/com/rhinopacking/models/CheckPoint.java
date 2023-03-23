package com.rhinopacking.models;

import java.util.Calendar;
import java.util.Date;

public class CheckPoint {
    int id_checkpoint;
    int codigo;
    float medidas;
    int cajas;

    boolean checkpoint_la;
    Date fecha_la;
    String observaciones_la;

    boolean checkpoint_ng;
    Date fecha_ng;
    String observaciones_ng;

    boolean checkpoint_gdl;
    Date fecha_gdl;
    String observaciones_gdl;

    boolean checkpoint_df;
    Date fecha_df;
    String observaciones_df;

    Fecha mFecha;

    public CheckPoint(int id_checkpoint, int codigo, float medidas, int cajas, boolean checkpoint_la, Date fecha_la, String observaciones_la, boolean checkpoint_ng, Date fecha_ng, String observaciones_ng, boolean checkpoint_gdl, Date fecha_gdl, String observaciones_gdl, boolean checkpoint_df, Date fecha_df, String observaciones_df) {
        this.id_checkpoint = id_checkpoint;
        this.codigo = codigo;
        this.medidas = medidas;
        this.cajas = cajas;
        this.checkpoint_la = checkpoint_la;
        this.fecha_la = fecha_la;
        this.observaciones_la = observaciones_la;
        this.checkpoint_ng = checkpoint_ng;
        this.fecha_ng = fecha_ng;
        this.observaciones_ng = observaciones_ng;
        this.checkpoint_gdl = checkpoint_gdl;
        this.fecha_gdl = fecha_gdl;
        this.observaciones_gdl = observaciones_gdl;
        this.checkpoint_df = checkpoint_df;
        this.fecha_df = fecha_df;
        this.observaciones_df = observaciones_df;
    }

    public CheckPoint(int codigo, float medidas, int cajas, Fecha mFecha, String observaciones_la) {
        this.codigo = codigo;
        this.medidas = medidas;
        this.cajas = cajas;
        this.mFecha = mFecha;
        this.checkpoint_la = true;
        this.observaciones_la = observaciones_la;
    }

    public int getId_checkpoint() {
        return id_checkpoint;
    }

    public int getCodigo() {
        return codigo;
    }

    public float getMedidas() {
        return medidas;
    }

    public int getCajas() {
        return cajas;
    }

    public boolean isCheckpoint_la() {
        return checkpoint_la;
    }

    public Fecha getmFecha() {
        return mFecha;
    }

    public Date getFecha_la() {
        return fecha_la;
    }

    public String getObservaciones_la() {
        return observaciones_la;
    }

    public boolean isCheckpoint_ng() {
        return checkpoint_ng;
    }

    public Date getFecha_ng() {
        return fecha_ng;
    }

    public String getObservaciones_ng() {
        return observaciones_ng;
    }

    public boolean isCheckpoint_gdl() {
        return checkpoint_gdl;
    }

    public Date getFecha_gdl() {
        return fecha_gdl;
    }

    public String getObservaciones_gdl() {
        return observaciones_gdl;
    }

    public boolean isCheckpoint_df() {
        return checkpoint_df;
    }

    public Date getFecha_df() {
        return fecha_df;
    }

    public String getObservaciones_df() {
        return observaciones_df;
    }


    public String getFechaString(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH) + 1)+"/"+calendar.get(Calendar.YEAR);
    }

    public String getFechaSQL(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH);
    }
}
