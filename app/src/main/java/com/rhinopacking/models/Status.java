package com.rhinopacking.models;

import java.util.Date;

public class Status {
    int id_stastus;
    int codigo;
    float medidas;
    int cajas;
    String status;
    Date fecha;

    public Status(int id_stastus, int codigo, float medidas, int cajas, String status, Date fecha) {
        this.id_stastus = id_stastus;
        this.codigo = codigo;
        this.medidas = medidas;
        this.cajas = cajas;
        this.status = status;
        this.fecha = fecha;
    }

    public int getId_stastus() {
        return id_stastus;
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
