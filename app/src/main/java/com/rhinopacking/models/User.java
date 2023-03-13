package com.rhinopacking.models;

public class User {
    String nombre;
    String telefono;
    boolean administrador;

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public User(String nombre, String telefono, boolean administrador) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.administrador = administrador;
    }

    public User(String nombre, String telefono, String administrador) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.administrador = administrador.equals("true");
    }
}
