package com.example.clases;



public class Contactos {
    private String nombre, id, Foto, Telefono;


    public Contactos() {
    }

    public Contactos( String id, String nombre, String Foto, String Telefono) {
        this.id = id;
        this.nombre = nombre;
        this.Foto = Foto;
        this.Telefono = Telefono;
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

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }
}
