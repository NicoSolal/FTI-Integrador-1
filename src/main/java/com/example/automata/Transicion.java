package com.example.automata;

public class Transicion {
    private String codigo;
    private Estado origen;
    private Estado destino;

    public Transicion(String codigo, Estado origen, Estado destino) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Estado getOrigen() {
        return origen;
    }

    public void setOrigen(Estado origen) {
        this.origen = origen;
    }

    public Estado getDestino() {
        return destino;
    }

    public void setDestino(Estado destino) {
        this.destino = destino;
    }
}