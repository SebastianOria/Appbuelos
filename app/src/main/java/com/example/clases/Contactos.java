package com.example.clases;



public class Contactos {
    private String nombre, id, profilepic;


    public Contactos() {
    }

    public Contactos(String nombre, String id, String profilepic) {
        this.id = id;
        this.nombre = nombre;
        this.profilepic = profilepic;
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

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }


}
