package com.example.automata;

import java.util.ArrayList;
import java.util.List;

public class Automata {
    private List<Estado> estados;
    private List<Estado> estadosFinalizadores;
    private String estadoInicial;
    private List<Transicion> transiciones;
    private List<String> alfabeto;
    

    public Automata() {
        this.estados = new ArrayList<>();
        this.estadosFinalizadores = new ArrayList<>();
        this.transiciones = new ArrayList<>();
        this.alfabeto = new ArrayList<>();
    }

    public Estado transicionar(String entrada) {
        Estado estadoActual = this.getEstadoInicial();
        for (char simbolo : entrada.toCharArray()) {
            estadoActual = this.transicion(estadoActual, String.valueOf(simbolo));
            if (estadoActual == null) {
                return null;
            }
        }
        
        return estadoActual;
    }
    
    private Estado transicion(Estado estadoActual, String simbolo) {
        for (Transicion transicion : this.transiciones) {
            if (transicion.getOrigen().equals(estadoActual) && transicion.getCodigo().equals(simbolo)) {
                return transicion.getDestino();
            }
        }

        return null;
    }

    public Estado agregarEstado(String nombre, boolean aceptador) {
        Estado estado = new Estado(nombre, aceptador);
        if(this.estados.isEmpty()) {
            this.estadoInicial = estado.getNombre();
        }

        this.estados.add(estado);
        return estado;
    }

    public void agregarTransicion(String codigo, Estado origen, Estado destino) {
        Transicion transicion = new Transicion(codigo, origen, destino);
        this.transiciones.add(transicion);
    }

    public Estado getEstado(String nombre) {
        for (Estado estado : this.estados) {
            if (estado.getNombre().equals(nombre)) {
                return estado;
            }
        }
        return null;
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public void setEstados(List<Estado> estados) {
        this.estados = estados;
    }

    public Estado getEstadoInicial() {
        return getEstado(this.estadoInicial);
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public List<Estado> getEstadosFinalizadores() {
        return estadosFinalizadores;
    }

    public void setEstadosFinalizadores(List<Estado> estadosFinalizadores) {
        this.estadosFinalizadores = estadosFinalizadores;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    public void setTransiciones(List<Transicion> transiciones) {
        this.transiciones = transiciones;
    }

    public List<String> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(List<String> alfabeto) {
        this.alfabeto = alfabeto;
    }
}