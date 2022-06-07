package com.example.clases;

import java.util.Date;

public class Mensaje {
    private String tipo;
    private String texto;
    private Date Fecha;
    private Date hora;

    public Mensaje(String tipo, String texto, Date fecha, Date hora) {
        this.tipo = tipo;
        this.texto = texto;
        Fecha = fecha;
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        Fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "tipo='" + tipo + '\'' +
                ", texto='" + texto + '\'' +
                ", Fecha=" + Fecha +
                ", hora=" + hora +
                '}';
    }
}
