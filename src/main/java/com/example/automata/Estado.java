package com.example.automata;

public class Estado {
    private String nombre;
    private boolean aceptador;

    public Estado(String nombre, boolean aceptador) {
        this.nombre = nombre;
        this.aceptador = aceptador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isAceptador() {
        return this.aceptador;
    }

    @Override
    public String toString() {
        return nombre + " aceptador=" + aceptador;
    }
}