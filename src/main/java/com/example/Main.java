package com.example;

import java.util.Scanner;

import com.example.automata.*;
import com.example.constructor.Constructor;
import com.example.conversor.Conversor;
import com.example.minimizador.Minimizador;
import com.example.validador.Validador;


public class Main {
    private static String rutaArchivoAutomata = "archivos/auto5.json";
    
    public static void main(String[] args) {
        Automata automata = new Automata();
        Constructor constructor = new Constructor();
        Conversor conversor = new Conversor();
        Validador validador = new Validador();
        Minimizador minimizador = new Minimizador();

        automata = constructor.construirDesdeJson(rutaArchivoAutomata);
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

        String rutaResultado = rutaArchivoAutomata.replace(".json", "_am.json");
        constructor.guardarEnJson(automata, rutaResultado);
        System.out.println("Automata minimizado guardado en: " + rutaResultado + "\n");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Ingrese una cadena a validar (o \"salir\" para terminar): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase("salir")) {
                break;
            }
            Estado estadoFinal = validador.transicionar(automata, entrada);
            if (estadoFinal != null && estadoFinal.isAceptador()) {
                System.out.println("La cadena \"" + entrada + "\" es aceptada.");
            } else {
                System.out.println("La cadena \"" + entrada + "\" no es aceptada.");
            }
        }
        scanner.close();
    }
}
