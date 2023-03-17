package com.rhinopacking.models;

import android.graphics.Bitmap;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Registro {


    public int getId_registro() {
        return id_registro;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public float getPrecio() {
        return precio;
    }

    public boolean isPagado() {
        return pagado;
    }

    public String getStatus() {
        return status;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Date getFecha_almacen() {
        return fecha_almacen;
    }

    public Date getFecha_entrega() {
        return fecha_entrega;
    }

    public Bitmap getFoto_recibido() {
        return foto_recibido;
    }

    public Bitmap getFoto_entrega() {
        return foto_entrega;
    }

    public void setFoto_recibido(Bitmap foto_recibido) {
        this.foto_recibido = foto_recibido;
    }

    public void setFoto_entrega(Bitmap foto_entrega) {
        this.foto_entrega = foto_entrega;
    }

    public Registro(String codigo, String nombre, String telefono, float precio, boolean pagado, String status, String observaciones, Date fecha_almacen, Date fecha_entrega,  boolean activo) {
       // foto_recibido = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.precio = precio;
        this.pagado = pagado;
        this.status = status;
        this.observaciones = observaciones;
        this.fecha_almacen = fecha_almacen;
        this.fecha_entrega = fecha_entrega;
        this.activo = activo;
    }



    public Registro(String codigo, String nombre, String telefono, float precio, boolean pagado, String status, String observaciones, Date fecha_almacen, Date fecha_entrega,  boolean activo, int metodo, int id_operador) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.precio = precio;
        this.pagado = pagado;
        this.status = status;
        this.observaciones = observaciones;
        this.fecha_almacen = fecha_almacen;
        this.fecha_entrega = fecha_entrega;
        this.activo = activo;
        this.metodo = metodo;
        this.id_operador = id_operador;
    }

    public Registro(String codigo, String nombre, float precio, String status, Date fecha_almacen,  boolean activo, int metodo) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.precio = precio;
        this.pagado = pagado;
        this.status = status;
        this.observaciones = observaciones;
        this.fecha_almacen = fecha_almacen;
        this.fecha_entrega = fecha_entrega;
        this.activo = activo;
        this.metodo = metodo;
        this.id_operador = id_operador;
    }

    public int getMetodo() {
        return metodo;
    }


    public Registro()
    {

    }


    public Registro(String codigo, String nombre, float precio, boolean pagado, int metodo)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.pagado = pagado;
        this.metodo = metodo;
    }

    public Registro(String codigo, String nombre, String telefono, float precio, boolean pagado, int metodo, String status, String observaciones, Bitmap foto_recibido, int id_operador) {

        this.foto_recibido = foto_recibido;
        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.precio = precio;
        this.pagado = pagado;
        this.metodo = metodo;
        this.status = status;
        this.observaciones = observaciones;
        this.id_operador = id_operador;
    }

    public Registro(String codigo, String nombre, String telefono, float precio, boolean pagado, int metodo, String status, String observaciones, Bitmap foto_recibido) {

        this.foto_recibido = foto_recibido;
        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.precio = precio;
        this.pagado = pagado;
        this.metodo = metodo;
        this.status = status;
        this.observaciones = observaciones;
    }




    public String getSemana() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek( Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek( 4 );
        calendar.setTime(fecha_almacen);
        return Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
    }



    public boolean isActivo() {
        return activo;
    }

    public void setId_registro(int id_registro) {
        this.id_registro = id_registro;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public void setMetodo(int metodo) {
        this.metodo = metodo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setFecha_almacen(Date fecha_almacen) {
        this.fecha_almacen = fecha_almacen;
    }

    public void setFecha_entrega(Date fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setId_operador(int id_operador) {
        this.id_operador = id_operador;
    }

    public int getId_operador() {
        return id_operador;
    }

    public Registro(String codigo, String nombre, String status, boolean pagado, Date fecha_recibio, boolean activo, float precio, String telefono, int id_operador) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.status = status;
        this.pagado = pagado;
        this.fecha_almacen = fecha_recibio;
        this.activo = activo;
        this.precio = precio;
        this.telefono = telefono;
        this.id_operador = id_operador;
    }

    int id_registro;
    String codigo;
    String nombre;

    int id_operador;

    String telefono;
    float precio;
    boolean pagado;

    int metodo;
    String status;
    String observaciones;
    Date fecha_almacen;
    Date fecha_entrega;
    Bitmap foto_recibido;
    Bitmap foto_entrega;

    boolean activo;

}
