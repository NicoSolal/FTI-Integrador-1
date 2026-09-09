package com.example.constructor;

import com.example.automata.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Constructor {
    public Constructor() { }

    public Automata construirDesdeJson(String rutaArchivo) {
        Automata automata = new Automata();
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            JsonNode rootNode = mapper.readTree(new File(rutaArchivo));
            
            String estadoInicial = rootNode.get("estadoInicial").asText();
            automata.setEstadoInicial(estadoInicial);
            
            JsonNode estadosNode = rootNode.get("estados");
            if (estadosNode.isArray()) {
                for (JsonNode estado : estadosNode) {
                    String nombre = estado.get("nombre").asText();
                    boolean aceptador = estado.get("aceptador").asBoolean();
                    
                    automata.agregarEstado(nombre, aceptador);
                }
            }
            
            JsonNode transicionesNode = rootNode.get("transiciones");
            if (transicionesNode.isArray()) {
                for (JsonNode transicion : transicionesNode) {
                    String origen = transicion.get("origen").asText();
                    String destino = transicion.get("destino").asText();
                    String simbolo = transicion.get("simbolo").asText();

                    automata.agregarTransicion(simbolo, automata.getEstado(origen), automata.getEstado(destino));
                }
            }
            
            JsonNode alfabetoNode = rootNode.get("alfabeto");
            if (alfabetoNode.isArray()) {
                List<String> alfabeto = new ArrayList<>();
                for (JsonNode simbolo : alfabetoNode) {
                    alfabeto.add(simbolo.asText());
                }
                
                automata.setAlfabeto(alfabeto);
            }
            
        } catch (Exception e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
        
        return automata;
    }
}