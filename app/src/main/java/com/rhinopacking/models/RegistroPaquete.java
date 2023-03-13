package com.rhinopacking.models;

import android.graphics.Bitmap;

public class RegistroPaquete {


    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }


    public RegistroPaquete(int id_paquete, String codigo, int cantidad, String medidas, Bitmap foto) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.medidas = medidas;
        this.foto = foto;
        this.id_paquete = id_paquete;
    }

    public RegistroPaquete(String codigo, int cantidad, String medidas, Bitmap foto) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.medidas = medidas;
        this.foto = foto;
    }

    public int getId() {
        return id_paquete;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getMedidas() {
        return medidas;
    }

    public Bitmap getFoto() {
        return foto;
    }

    int id_paquete=0;
    String codigo;
    int cantidad;
    String medidas;
    Bitmap foto;

}
