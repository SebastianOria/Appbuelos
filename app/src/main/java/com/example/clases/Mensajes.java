package com.example.clases;

public class Mensajes {
    private String de, mensaje, tipo;

    public Mensajes() {

    }

    public Mensajes(String de, String mensaje, String para) {
        this.de = de;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
