package com.rhinopacking.models;

import android.graphics.Bitmap;

public class RegistroComplemento {

    int id_complemento;
    String codigo;
    Bitmap foto;

    public RegistroComplemento(int id_complemento, String codigo, Bitmap foto) {
        this.id_complemento = id_complemento;
        this.codigo = codigo;
        this.foto = foto;
    }

    public int getId_complemento() {
        return id_complemento;
    }

    public void setId_complemento(int id_complemento) {
        this.id_complemento = id_complemento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }
}
