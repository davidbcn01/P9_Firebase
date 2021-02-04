package com.example.p9_firebase;

public class Mensaje {
    public String mensaje;
    public String fecha;
    public String nombre;
    public String email;
    public String foto;

    public Mensaje(String mensaje, String fecha, String nombre, String email, String foto, String meme) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.nombre = nombre;
        this.email = email;
        this.foto = foto;
        this.meme = meme;
    }

    public String meme;
    public Mensaje(String mensaje, String fecha, String nombre, String email, String foto) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.nombre = nombre;
        this.email = email;
        this.foto = foto;
    }
}
