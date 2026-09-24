package com.example.minimizador;

import com.example.automata.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Minimizador {
    public Minimizador() { }

    public Automata minimizar(Automata afd) {
        afd.eliminarInalcanzables();

        List<List<Estado>> clases = particionInicial(afd);
        int cantidadAnterior;
        do {
            cantidadAnterior = clases.size();
            clases = refinar(afd, clases);
        } while (clases.size() != cantidadAnterior);

        return construir(afd, clases);
    }

    private List<List<Estado>> particionInicial(Automata afd) {
        List<List<Estado>> result = new ArrayList<>();
        List<Estado> noFinalizadores = new ArrayList<>();
        List<Estado> finalizadores = new ArrayList<>();

        for (Estado estado : afd.getEstados()) {
            if (estado.isAceptador()) {
                if (finalizadores.isEmpty()) result.add(finalizadores);
                finalizadores.add(estado);
            } else {
                if (noFinalizadores.isEmpty()) result.add(noFinalizadores);
                noFinalizadores.add(estado);
            }
        }
        return result;
    }

    private List<List<Estado>> refinar(Automata afd, List<List<Estado>> clases) {
        List<List<Estado>> result = new ArrayList<>();
        for (Estado estado : afd.getEstados()) {
            List<Estado> claseEncontrada = null;
            for (List<Estado> clase : result) {
                if (sonEquivalentes(afd, clases, estado, clase.get(0))) {
                    claseEncontrada = clase;
                    break;
                }
            }

            if (claseEncontrada == null) {
                claseEncontrada = new ArrayList<>();
                result.add(claseEncontrada);
            }
            claseEncontrada.add(estado);
        }
        return result;
    }

    private boolean sonEquivalentes(Automata afd, List<List<Estado>> clases, Estado a, Estado b) {
        if (numeroDeClase(clases, a) != numeroDeClase(clases, b)) return false;

        for (String simbolo : afd.getAlfabeto()) {
            if (numeroDeClase(clases, destino(afd, a, simbolo)) != numeroDeClase(clases, destino(afd, b, simbolo))) {
                return false;
            }
        }
        return true;
    }

    // Si no hay transición (estado null) devuelve -1, como si fuera a un estado err no finalizador
    private int numeroDeClase(List<List<Estado>> clases, Estado estado) {
        for (int i = 0; i < clases.size(); i++) {
            if (clases.get(i).contains(estado)) return i;
        }
        return -1;
    }

    private Estado destino(Automata afd, Estado origen, String simbolo) {
        for (Transicion transicion : afd.getTransiciones()) {
            if (transicion.getOrigen().equals(origen) && transicion.getCodigo().equals(simbolo)) {
                return transicion.getDestino();
            }
        }
        return null;
    }

    private Automata construir(Automata afd, List<List<Estado>> clases) {
        Automata result = new Automata();
        result.setAlfabeto(new ArrayList<>(afd.getAlfabeto()));

        for (int i = 0; i < clases.size(); i++) {
            result.agregarEstado("[" + i + "]", clases.get(i).get(0).isAceptador());
            String estados = clases.get(i).stream().map(estado -> "" + estado.getNombre() + "").collect(Collectors.joining(", "));
            System.out.println("Estado agregado: [" + i + "]: (" + estados + ")");
        }
        result.setEstadoInicial("[" + numeroDeClase(clases, afd.getEstadoInicial()) + "]");

        for (int i = 0; i < clases.size(); i++) {
            Estado representante = clases.get(i).get(0);
            for (String simbolo : afd.getAlfabeto()) {
                Estado destino = destino(afd, representante, simbolo);
                if (destino != null) {
                    result.agregarTransicion(simbolo, result.getEstado("[" + i + "]"), result.getEstado("[" + numeroDeClase(clases, destino) + "]"));
                }
            }
        }
        return result;
    }
}
