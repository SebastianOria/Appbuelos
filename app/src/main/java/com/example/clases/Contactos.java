package com.example.clases;



public class Contactos {
    private String nombre, id, Foto, token;


    public Contactos() {
    }

    public Contactos( String id, String nombre, String Foto, String token) {
        this.id = id;
        this.nombre = nombre;
        this.Foto = Foto;
        this.token = token;

    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }


}
