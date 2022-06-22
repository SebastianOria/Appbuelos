package com.example.clases;

import java.util.Date;

public class Mensaje {
    private String nombre, id, lastMessage, profilepic;
    private int unseenMessajes;

    public Mensaje() {
    }

    public Mensaje(String nombre, String profilepic) {
        this.nombre = nombre;
        this.profilepic = profilepic;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }


}
