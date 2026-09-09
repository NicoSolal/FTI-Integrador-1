package com.example;

import com.example.automata.*;
import com.example.constructor.Constructor;

public class Main {
    public static void main(String[] args) {
        Automata automata = new Automata();
        Constructor constructor = new Constructor();

        String rutaArchivo = "archivos/auto1.json";
        automata = constructor.construirDesdeJson(rutaArchivo);

        String[] entradas = {"a", "b", "ab", "ba", "abababa"};
        for (String entrada : entradas) {
            Estado estadoFinal = automata.transicionar(entrada);
            if (estadoFinal != null && estadoFinal.isAceptador()) {
                System.out.println("La cadena \"" + entrada + "\" es aceptada.");
            } else {
                System.out.println("La cadena \"" + entrada + "\" no es aceptada.");
            }
        }
    }
}
