package com.rhinopacking.models;

import java.util.Date;

public class Status {
    int id_status;
    int codigo;
    float medidas;
    int cajas;
    String status;
    Date fecha;

    Fecha mFecha;

    public Status(int id_status, int codigo, float medidas, int cajas, String status, Date fecha) {
        this.id_status = id_status;
        this.codigo = codigo;
        this.medidas = medidas;
        this.cajas = cajas;
        this.status = status;
        this.fecha = fecha;
    }

    public Fecha getmFecha() {
        return mFecha;
    }

    public Status(int codigo, float medidas, int cajas, String status, Fecha mFecha) {
        this.id_status = id_status;
        this.codigo = codigo;
        this.medidas = medidas;
        this.cajas = cajas;
        this.status = status;
        this.mFecha = mFecha;
    }

    public int getId_status() {
        return id_status;
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

    public String getStatus() {
        return status;
    }

    public Date getFecha() {
        return fecha;
    }
}
