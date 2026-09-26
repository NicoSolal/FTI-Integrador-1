El programa lee un archivo json que contiene un autómata y hace lo siguiente para minimizarlo.

1. Si es no determinista (AFND), lo convierte a determinista (AFD).
2. Minimiza el AFD.
3. Valida el resultado:
   - el lenguaje del AFD minimizado es equivalente al del autómata original, para esto se usa el
     método de árbol de moore
   - la cantidad de estados se redujo o se mantuvo.
4. Guarda el autómata minimizado en un nuevo archivo JSON.

REQUISITOS:

- Java 17 o superior
- Maven
- make (para usar makefile, aunque opcional)

CON MAKE:

- make (limpia, compila y ejecuta)
- make compile (solo compila)
- make run (solo ejecuta)
- make clean (borra la carpeta target/)

ELEGIR EL AUTÓMATA DE ENTRADA:

El archivo de entrada se define en Main.java:

private static String rutaArchivo = "archivos/autox.json";

VALIDACIÓN DE CADENAS DE ENTRADA:

Al ejecutar el programa y procesar el autómata al llegar al minimizado se entra en un
bucle en el que se puede inputar cualquier cadena que se quiera, y se va a validar,
devolviendo un mensaje diciendo si se aceptó o no.
