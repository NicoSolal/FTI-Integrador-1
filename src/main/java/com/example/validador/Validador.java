package com.example.validador;

import com.example.automata.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Validador {
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
        List<String> alfabeto = new ArrayList<>(original.getAlfabeto());
        for (String simbolo : minimizado.getAlfabeto()) {
            if (!alfabeto.contains(simbolo)) alfabeto.add(simbolo);
        }

        List<Estado> inicialOriginal = new ArrayList<>(List.of(original.getEstadoInicial()));
        List<Estado> inicialMinimizado = new ArrayList<>(List.of(minimizado.getEstadoInicial()));

        List<List<Estado>> pendientesOriginal = new ArrayList<>(List.of(inicialOriginal));
        List<List<Estado>> pendientesMinimizado = new ArrayList<>(List.of(inicialMinimizado));
        List<String> visitados = new ArrayList<>(List.of(clavePar(inicialOriginal, inicialMinimizado)));

        System.out.println("Arbol de Moore:");
        while (!pendientesOriginal.isEmpty()) {
            List<Estado> actualOriginal = pendientesOriginal.remove(0);
            List<Estado> actualMinimizado = pendientesMinimizado.remove(0);

            if (tieneAceptador(actualOriginal) != tieneAceptador(actualMinimizado)) {
                System.out.println("  " + clavePar(actualOriginal, actualMinimizado)
                        + " -> un estado es aceptador y el otro no, no son equivalentes.");
                return false;
            }

            for (String simbolo : alfabeto) {
                List<Estado> siguienteOriginal = destinos(original, actualOriginal, simbolo);
                List<Estado> siguienteMinimizado = destinos(minimizado, actualMinimizado, simbolo);
                String clave = clavePar(siguienteOriginal, siguienteMinimizado);

                boolean repetido = visitados.contains(clave);
                System.out.println("  " + clavePar(actualOriginal, actualMinimizado) + " --" + simbolo + "--> "
                        + clave + (repetido ? " (repetido)" : ""));
                if (!repetido) {
                    visitados.add(clave);
                    pendientesOriginal.add(siguienteOriginal);
                    pendientesMinimizado.add(siguienteMinimizado);
                }
            }
        }
        return true;
    }

    private List<Estado> destinos(Automata automata, List<Estado> estados, String simbolo) {
        List<Estado> result = new ArrayList<>();
        for (Transicion transicion : automata.getTransiciones()) {
            if (estados.contains(transicion.getOrigen()) && transicion.getCodigo().equals(simbolo)
                    && !result.contains(transicion.getDestino())) {
                result.add(transicion.getDestino());
            }
        }
        return result;
    }

    private boolean tieneAceptador(List<Estado> estados) {
        for (Estado estado : estados) {
            if (estado.isAceptador()) return true;
        }
        return false;
    }

    private String clavePar(List<Estado> estadosOriginal, List<Estado> estadosMinimizado) {
        return "(" + nombres(estadosOriginal) + ", " + nombres(estadosMinimizado) + ")";
    }

    private String nombres(List<Estado> estados) {
        List<String> nombres = new ArrayList<>();
        for (Estado estado : estados) nombres.add(estado.getNombre());
        Collections.sort(nombres);
        return "{" + String.join(",", nombres) + "}";
    }

    public boolean seRedujo(Automata afd, Automata minimizado) {
        return minimizado.getEstados().size() <= afd.getEstados().size();
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
