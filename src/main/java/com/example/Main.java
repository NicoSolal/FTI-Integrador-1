package com.example;

import com.example.automata.*;
import com.example.constructor.Constructor;
import com.example.conversor.Conversor;
import com.example.minimizador.Minimizador;
import com.example.validador.Validador;

public class Main {
    public static void main(String[] args) {
        Automata automata = new Automata();
        Constructor constructor = new Constructor();
        Conversor conversor = new Conversor();
        Validador validador = new Validador();
        Minimizador minimizador = new Minimizador();

        String rutaArchivo = "archivos/auto3.json";
        automata = constructor.construirDesdeJson(rutaArchivo);
        Automata original = automata.clonar();

        System.out.println(automata);
        System.out.println("Es deterministico: " + validador.esDeterministico(automata) + "\n");

        if (!validador.esDeterministico(automata)) {
            automata = conversor.convertirADeterministico(automata);
            System.out.println("Convertido a AFD:");
            System.out.println(automata);
            System.out.println("Es deterministico: " + validador.esDeterministico(automata) + "\n");
        }

        Automata afd = automata.clonar();
        automata = minimizador.minimizar(automata);
        System.out.println("Minimizado:");
        System.out.println(automata);

        System.out.println("Es equivalente al original: " + validador.sonEquivalentes(original, automata));
        System.out.println("Estados AFD: " + afd.getEstados().size() + ", estados minimizado: " + automata.getEstados().size()
                + " -> se redujo o mantuvo: " + validador.seRedujo(afd, automata) + "\n");

        String rutaResultado = rutaArchivo.replace(".json", "_am.json");
        constructor.guardarEnJson(automata, rutaResultado);
        System.out.println("Automata minimizado guardado en: " + rutaResultado + "\n");

        String[] entradas = {"01", "000", "000101", "110"};
        for (String entrada : entradas) {
            Estado estadoFinal = validador.transicionar(automata, entrada);
            if (estadoFinal != null && estadoFinal.isAceptador()) {
                System.out.println("La cadena \"" + entrada + "\" es aceptada.");
            } else {
                System.out.println("La cadena \"" + entrada + "\" no es aceptada.");
            }
        }
    }
}
