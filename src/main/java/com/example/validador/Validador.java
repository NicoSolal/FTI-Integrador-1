package com.example.validador;

import com.example.automata.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Validador {
    private static final int CANTIDAD_CADENAS = 1000;
    private static final int LARGO_MAXIMO = 10;

    public Validador() { }

    public Estado transicionar(Automata automata, String entrada) {
        Estado estadoActual = automata.getEstadoInicial();
        for (char simbolo : entrada.toCharArray()) {
            estadoActual = this.transicion(automata, estadoActual, String.valueOf(simbolo));
            if (estadoActual == null) {
                return null;
            }
        }

        return estadoActual;
    }

    private Estado transicion(Automata automata, Estado estadoActual, String simbolo) {
        for (Transicion transicion : automata.getTransiciones()) {
            if (transicion.getOrigen().equals(estadoActual) && transicion.getCodigo().equals(simbolo)) {
                System.out.println("Transición: " + transicion.getOrigen().getNombre() + " --" + simbolo + "--> " + transicion.getDestino().getNombre());
                return transicion.getDestino();
            }
        }

        return null;
    }

    public boolean aceptaCadena(Automata automata, String cadena) {
        List<Estado> actuales = new ArrayList<>();
        actuales.add(automata.getEstadoInicial());

        for (char simbolo : cadena.toCharArray()) {
            List<Estado> siguientes = new ArrayList<>();
            for (Transicion transicion : automata.getTransiciones()) {
                if (actuales.contains(transicion.getOrigen()) && transicion.getCodigo().equals(String.valueOf(simbolo))
                        && !siguientes.contains(transicion.getDestino())) {
                    siguientes.add(transicion.getDestino());
                }
            }
            actuales = siguientes;
        }

        for (Estado estado : actuales) {
            if (estado.isAceptador()) return true;
        }
        return false;
    }

    public boolean sonEquivalentes(Automata original, Automata minimizado) {
        for (String cadena : generarCadenas(original.getAlfabeto())) {
            if (aceptaCadena(original, cadena) != aceptaCadena(minimizado, cadena)) {
                System.out.println("La cadena \"" + cadena + "\" da distinto resultado en ambos automatas.");
                return false;
            }
        }
        return true;
    }

    public boolean seRedujo(Automata afd, Automata minimizado) {
        return minimizado.getEstados().size() <= afd.getEstados().size();
    }

    private List<String> generarCadenas(List<String> alfabeto) {
        List<String> result = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < CANTIDAD_CADENAS; i++) {
            int largo = random.nextInt(LARGO_MAXIMO + 1);
            String cadena = "";
            for (int j = 0; j < largo; j++) {
                cadena += alfabeto.get(random.nextInt(alfabeto.size()));
            }
            result.add(cadena);
        }
        return result;
    }

    public boolean esDeterministico(Automata automata) {
        for(Estado estado : automata.getEstados()) {
            if(tieneDosTransicionesIguales(automata, estado)) return false;
        }
        return true;
    }

    private boolean tieneDosTransicionesIguales(Automata automata, Estado estado) {
        List<Transicion> transicionesEstado = new ArrayList<>();
        for(Transicion transicion : automata.getTransiciones())
            if(transicion.getOrigen().equals(estado)) transicionesEstado.add(transicion);

        for(int i = 0; i < transicionesEstado.size(); i++) {
            for(int j = i + 1; j < transicionesEstado.size(); j++) {
                if(transicionesEstado.get(i).getCodigo().equals(transicionesEstado.get(j).getCodigo())) {
                    return true;
                }
            }
        }
        return false;
    }
}
