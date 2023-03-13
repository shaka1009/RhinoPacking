package com.rhinopacking.models;

public class Operador {

    int id_usuario;
    String nombre;
    String telefono;

    public Operador(int id_usuario, String nombre, String telefono) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_operador) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
