package com.rhinopacking.models;

public class Fecha {
    int mes;
    int year;

    public int getMes() {
        return mes;
    }

    public int getYear() {
        return year;
    }

    public String getFecha()
    {
        String fecha="";

        switch (mes)
        {
            case 1:
                fecha = "Enero";
            break;
            case 2:
                fecha = "Febrero";
            break;

            case 3:
                fecha = "Marzo";
            break;

            case 4:
                fecha = "Abril";
            break;

            case 5:
                fecha = "Mayo";
            break;

            case 6:
                fecha = "Junio";
            break;

            case 7:
                fecha = "Julio";
            break;

            case 8:
                fecha = "Agosto";
            break;

            case 9:
                fecha = "Septiembre";
            break;

            case 10:
                fecha = "Octubre";
            break;

            case 11:
                fecha = "Noviembre";
            break;

            case 12:
                fecha = "Diciembre";
            break;
        }

        return fecha + " - " + year;
    }

    public Fecha(int mes, int year) {
        this.mes = mes;
        this.year = year;
    }
}
