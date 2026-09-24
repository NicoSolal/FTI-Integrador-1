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

    public Automata clonar() {
        Automata result = new Automata();
        result.setAlfabeto(new ArrayList<>(this.alfabeto));

        for (Estado estado : this.estados) {
            result.agregarEstado(estado.getNombre(), estado.isAceptador());
        }
        result.setEstadoInicial(this.estadoInicial);

        for (Transicion transicion : this.transiciones) {
            result.agregarTransicion(transicion.getCodigo(), result.getEstado(transicion.getOrigen().getNombre()), result.getEstado(transicion.getDestino().getNombre()));
        }
        return result;
    }

    public void eliminarInalcanzables() {
        List<Estado> alcanzables = new ArrayList<>();
        alcanzables.add(this.getEstadoInicial());

        for (int i = 0; i < alcanzables.size(); i++) {
            for (Transicion transicion : this.transiciones) {
                if (transicion.getOrigen().equals(alcanzables.get(i)) && !alcanzables.contains(transicion.getDestino())) {
                    alcanzables.add(transicion.getDestino());
                }
            }
        }

        this.estados.removeIf(estado -> !alcanzables.contains(estado));
        this.transiciones.removeIf(transicion -> !alcanzables.contains(transicion.getOrigen()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Estado estado : estados) {
            sb.append(estado.toString()).append("\n");
        }
        sb.append("Transiciones:\n");
        for (Transicion transicion : transiciones) {
            sb.append(transicion.getOrigen().getNombre()).append(" --").append(transicion.getCodigo())
              .append("--> ").append(transicion.getDestino().getNombre()).append("\n");
        }
        return "Automata con estados: \n" + sb.toString();
    }
}