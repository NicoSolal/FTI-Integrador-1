package com.example.conversor;

import com.example.automata.*;

import java.util.ArrayList;
import java.util.List;

public class Conversor {
    public Conversor() { }

    public Automata convertirADeterministico(Automata afnd) {
        Automata result = new Automata();
        result.setAlfabeto(new ArrayList<>(afnd.getAlfabeto()));

        List<List<Estado>> conjuntos = crearConjuntos(afnd);
        for (List<Estado> conjunto : conjuntos) {
            result.agregarEstado(nombreConjunto(conjunto), tieneAceptador(conjunto));
        }
        result.setEstadoInicial(nombreConjunto(List.of(afnd.getEstadoInicial())));

        for (List<Estado> conjunto : conjuntos) {
            for (String simbolo : afnd.getAlfabeto()) {
                List<Estado> destino = destinos(afnd, conjunto, simbolo);
                if (!destino.isEmpty()) {
                    result.agregarTransicion(simbolo, result.getEstado(nombreConjunto(conjunto)), result.getEstado(nombreConjunto(destino)));
                }
            }
        }

        result.eliminarInalcanzables();

        return result;
    }

    private List<List<Estado>> crearConjuntos(Automata afnd) {
        List<List<Estado>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        for (Estado estado : afnd.getEstados()) {
            List<List<Estado>> nuevos = new ArrayList<>();
            for (List<Estado> conjunto : result) {
                List<Estado> copia = new ArrayList<>(conjunto);
                copia.add(estado);
                nuevos.add(copia);
            }
            result.addAll(nuevos);
        }

        result.remove(0);
        return result;
    }

    private List<Estado> destinos(Automata afnd, List<Estado> conjunto, String simbolo) {
        List<Estado> result = new ArrayList<>();
        for (Estado estado : afnd.getEstados()) {
            for (Transicion transicion : afnd.getTransiciones()) {
                if (conjunto.contains(transicion.getOrigen()) && transicion.getCodigo().equals(simbolo)
                        && transicion.getDestino().equals(estado)) {
                    result.add(estado);
                    break;
                }
            }
        }
        return result;
    }

    private String nombreConjunto(List<Estado> conjunto) {
        List<String> nombres = new ArrayList<>();
        for (Estado estado : conjunto) nombres.add(estado.getNombre());
        return "<" + String.join(", ", nombres) + ">";
    }

    private boolean tieneAceptador(List<Estado> conjunto) {
        for (Estado estado : conjunto) {
            if (estado.isAceptador()) return true;
        }
        return false;
    }
}
