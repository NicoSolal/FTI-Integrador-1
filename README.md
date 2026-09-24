# FTI-Integrador-1

Trabajo integrador 1 de Fundamentos Teóricos de la Informática.

El programa lee un autómata finito desde un archivo JSON y hace lo siguiente:

1. Si es **no determinista (AFND)**, lo convierte a **determinista (AFD)**.
2. **Minimiza** el AFD.
3. **Valida** el resultado:
   - el lenguaje del AFD minimizado es equivalente al del autómata original;
   - la cantidad de estados se redujo o se mantuvo.
4. Guarda el autómata minimizado en un nuevo archivo JSON.

---

## Índice

- [Requisitos](#requisitos)
- [Cómo ejecutarlo](#cómo-ejecutarlo)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Formato de los archivos JSON](#formato-de-los-archivos-json)
- [Explicación del código](#explicación-del-código)
  - [Representación del autómata](#representación-del-autómata-automata)
  - [Lectura y escritura de archivos](#lectura-y-escritura-de-archivos-constructor)
  - [Conversión AFND → AFD](#conversión-afnd--afd-conversor)
  - [Minimización](#minimización-minimizador)
  - [Validación](#validación-validador)
  - [Main](#main)
- [Salida del programa](#salida-del-programa)
- [Limitaciones](#limitaciones)

---

## Requisitos

- Java 17 o superior
- Maven
- `make` (opcional, para usar los atajos del `makefile`)

La única dependencia es [Jackson](https://github.com/FasterXML/jackson), para leer y escribir JSON. Maven la descarga solo.

## Cómo ejecutarlo

Con `make`:

```bash
make            # limpia, compila y ejecuta
make compile    # solo compila
make run        # solo ejecuta
make clean      # borra la carpeta target/
```

O directamente con Maven:

```bash
mvn compile exec:java
```

### Elegir el autómata de entrada

El archivo de entrada se define en `Main.java`:

```java
String rutaArchivo = "archivos/auto2.json";
```

Para procesar otro autómata, cambiá esa ruta y volvé a ejecutar. El resultado se guarda en la misma carpeta, con el mismo nombre y el sufijo `_am` (**a**utómata **m**inimizado):

| Entrada               | Salida                   |
|-----------------------|--------------------------|
| `archivos/auto1.json` | `archivos/auto1_am.json` |
| `archivos/auto2.json` | `archivos/auto2_am.json` |

### Autómatas de ejemplo

| Archivo               | Tipo | Lenguaje                                                      |
|-----------------------|------|---------------------------------------------------------------|
| `archivos/auto1.json` | AFD  | `a(ba)*`: empieza con `a` y alterna `b`, `a`                   |
| `archivos/auto2.json` | AFND | Cadenas sobre `{a, b}` que terminan en `ab`                    |

---

## Estructura del proyecto

```
FTI-Integrador-1/
├── archivos/                      Autómatas de entrada (y los _am.json generados)
├── apuntes                        Apuntes de teoría con los pasos de cada algoritmo
├── makefile
├── pom.xml
└── src/main/java/com/example/
    ├── Main.java                  Programa principal
    ├── automata/                  Representación del autómata
    │   ├── Automata.java
    │   ├── Estado.java
    │   └── Transicion.java
    ├── constructor/               Lectura/escritura de archivos
    │   └── Constructor.java
    ├── conversor/                 Conversión AFND → AFD
    │   └── Conversor.java
    ├── minimizador/               Minimización
    │   └── Minimizador.java
    └── validador/                 Validación de cadenas y del resultado
        └── Validador.java
```

Cada módulo pedido en la consigna tiene su propio paquete. Las clases que operan sobre un autómata (`Constructor`, `Conversor`, `Minimizador`, `Validador`) se crean con `new` y reciben el `Automata` como parámetro. La clase `Automata` solo guarda los datos.

---

## Formato de los archivos JSON

La entrada y la salida usan el mismo formato:

```json
{
  "estadoInicial": "q0",
  "estados": [
    { "nombre": "q0", "aceptador": false },
    { "nombre": "q1", "aceptador": true }
  ],
  "transiciones": [
    { "origen": "q0", "destino": "q1", "simbolo": "a" },
    { "origen": "q1", "destino": "q0", "simbolo": "b" }
  ],
  "alfabeto": ["a", "b"]
}
```

| Campo            | Descripción                                                                  |
|------------------|------------------------------------------------------------------------------|
| `estadoInicial`  | Nombre del estado inicial (q0).                                              |
| `estados`        | Lista de estados (Q). `aceptador: true` indica que pertenece a F.            |
| `transiciones`   | Función de transición (Delta). Cada elemento es `Delta(origen, simbolo) = destino`. |
| `alfabeto`       | Símbolos del alfabeto (Sigma).                                               |

- Un autómata es **no determinista** si algún estado tiene dos o más transiciones con el mismo símbolo. En `auto2.json`, `q0` tiene dos transiciones con `a`.
- No hace falta que todos los estados tengan transición con todos los símbolos. Si falta una transición, la cadena se rechaza.
- Cada símbolo tiene que ser **un solo carácter**, porque las cadenas se leen de a un carácter.

---

## Explicación del código

La teoría detrás de cada algoritmo está en el archivo [`apuntes`](apuntes). El código sigue esos mismos pasos.

### Representación del autómata (`automata/`)

Modela el autómata **M = (Q, Sigma, Delta, q0, F)**.

- **`Estado`**: un nombre y si es aceptador (pertenece a F).
- **`Transicion`**: estado origen, símbolo (`codigo`) y estado destino.
- **`Automata`**: la lista de estados (Q), la lista de transiciones (Delta), el alfabeto (Sigma) y el nombre del estado inicial (q0).

| Método                                    | Qué hace                                                                   |
|-------------------------------------------|----------------------------------------------------------------------------|
| `agregarEstado(nombre, aceptador)`        | Agrega un estado. El primero que se agrega queda como inicial.             |
| `agregarTransicion(simbolo, origen, destino)` | Agrega una transición.                                                |
| `getEstado(nombre)`                       | Busca un estado por nombre.                                                |
| `clonar()`                                | Devuelve una copia independiente del autómata.                             |
| `eliminarInalcanzables()`                 | Borra los estados a los que no se llega desde el inicial, y sus transiciones. Lo usan el conversor y el minimizador. |
| `toString()`                              | Muestra los estados y las transiciones.                                    |

`eliminarInalcanzables()` recorre el autómata a partir del estado inicial. Arranca con una lista que tiene solo el inicial y, para cada estado de la lista, agrega los destinos de sus transiciones que todavía no estén. Al final elimina todo lo que no quedó en la lista.

### Lectura y escritura de archivos (`Constructor`)

| Método                                  | Qué hace                                                           |
|-----------------------------------------|--------------------------------------------------------------------|
| `construirDesdeJson(rutaArchivo)`       | Lee un JSON con el [formato de arriba](#formato-de-los-archivos-json) y devuelve el `Automata`. |
| `guardarEnJson(automata, rutaArchivo)`  | Escribe el autómata en un JSON con el mismo formato.               |

### Conversión AFND → AFD (`Conversor`)

`convertirADeterministico(afnd)` devuelve un AFD nuevo y no modifica el AFND. Sigue los pasos de los apuntes:

**1. Crear los estados conjunto.** Cada estado del AFD es un conjunto de estados del AFND y se nombra `<q0, q1, ...>`. Se generan todos los subconjuntos no vacíos (`crearConjuntos`). Empieza con el conjunto vacío y, por cada estado del AFND, copia los conjuntos que ya tiene y le agrega ese estado a cada copia:

```
inicio:     []
con q0:     [], [q0]
con q1:     [], [q0], [q1], [q0, q1]
con q2:     [], [q0], [q1], [q0, q1], [q2], [q0, q2], [q1, q2], [q0, q1, q2]
```

Al final se saca el conjunto vacío. Un estado conjunto es **aceptador** si contiene al menos un estado aceptador. El estado inicial del AFD es `<q0>`.

**2. Definir las transiciones.** Para cada conjunto y cada símbolo del alfabeto se juntan todos los destinos posibles (`destinos`). Por ejemplo, en `auto2.json`, desde `<q0>` con `a` se puede ir a `q0` o a `q1`, así que la transición es `<q0> --a--> <q0, q1>`. Si no hay ningún destino, no se crea la transición.

**3. Eliminar los inalcanzables.** Se llama a `eliminarInalcanzables()`. De los 7 conjuntos que se generan para `auto2.json`, solo quedan `<q0>`, `<q0, q1>` y `<q0, q2>`.

### Minimización (`Minimizador`)

`minimizar(afd)` devuelve el AFD mínimo con el método de **clases k-equivalentes**. Sigue los pasos de los apuntes:

**1. Depuración.** Se eliminan los estados inalcanzables. Esto **modifica el AFD que recibe**; por eso `Main` guarda un clon antes de minimizar.

**2. Partición inicial (clases 0-equivalentes).** Se separan los estados en dos clases: no finalizadores y finalizadores.

**3. Refinamiento iterativo (clases k-equivalentes).** Dos estados siguen en la misma clase si:

- estaban en la misma clase en la iteración anterior, y
- para **cada símbolo** del alfabeto, sus transiciones van a la **misma clase**.

Se repite mientras cambie la cantidad de clases. Cuando deja de cambiar, las clases son las definitivas.

Si un estado **no tiene transición** con un símbolo, se toma como si fuera a un estado "err" no finalizador, que no pertenece a ninguna clase (en el código, la clase `-1`). No se agrega ningún estado nuevo al autómata. Por lo tanto, un estado con transición y otro sin transición para el mismo símbolo nunca quedan en la misma clase.

**4. Construcción.** Cada clase pasa a ser un estado del autómata mínimo:

- Las clases se nombran `[0]`, `[1]`, `[2]`…, en el orden en que aparecen los estados en el AFD. La clase del estado inicial siempre es `[0]`.
- Una clase es aceptadora si sus estados son aceptadores (todos lo son o ninguno lo es).
- Las transiciones de la clase son las del primer estado de la clase, llevadas a su clase destino.

**Ejemplo.** Un AFD con `q0`, `q1` (aceptador) y `q2`, donde `q0` y `q2` tienen las mismas transiciones:

```
q0 --a--> q1    q0 --b--> q2
q1 --a--> q1    q1 --b--> q2
q2 --a--> q1    q2 --b--> q0

Clases 0-equivalentes: {q0, q2} {q1}
Clases 1-equivalentes: {q0, q2} {q1}     <- no cambió, termina

Resultado:
[0] = {q0, q2}   [0] --a--> [1]   [0] --b--> [0]
[1] = {q1}       [1] --a--> [1]   [1] --b--> [0]
```

### Validación (`Validador`)

| Método                                  | Qué hace                                                                 |
|-----------------------------------------|--------------------------------------------------------------------------|
| `esDeterministico(automata)`            | `true` si ningún estado tiene dos transiciones con el mismo símbolo.     |
| `transicionar(automata, cadena)`        | Recorre un **AFD** con la cadena y devuelve el estado en el que termina (o `null` si falta una transición). Imprime cada transición que hace. |
| `aceptaCadena(automata, cadena)`        | `true` si el autómata acepta la cadena. Funciona con **AFND y AFD** porque lleva una lista de todos los estados en los que puede estar a la vez. No imprime nada. |
| `sonEquivalentes(original, minimizado)` | Prueba 1000 cadenas al azar en ambos autómatas y verifica que den el mismo resultado. |
| `seRedujo(afd, minimizado)`             | `true` si el minimizado tiene la misma cantidad de estados que el AFD o menos. |

**Equivalencia de lenguajes.** `sonEquivalentes` genera 1000 cadenas al azar, de largo entre 0 y 10, con símbolos del alfabeto. La cadena vacía puede aparecer entre ellas. Cada cadena se prueba con `aceptaCadena` en el **autómata original** (el AFND leído del archivo) y en el **minimizado**. Si alguna da distinto resultado, se imprime y devuelve `false`. La cantidad de cadenas y el largo máximo son las constantes `CANTIDAD_CADENAS` y `LARGO_MAXIMO`, al principio de la clase.

**Cantidad de estados.** `seRedujo` compara el minimizado contra el **AFD** (el resultado de la conversión, antes de minimizar).

### Main

`Main.java` ejecuta todo el proceso en orden:

1. Lee el autómata de `rutaArchivo` y guarda un clon como `original`.
2. Lo muestra e indica si es determinista.
3. Si no es determinista, lo convierte a AFD y lo muestra.
4. Guarda un clon del AFD como `afd` y minimiza el autómata.
5. Muestra el minimizado y las dos validaciones.
6. Guarda el minimizado en `<nombre>_am.json`.
7. Prueba algunas cadenas de ejemplo con el minimizado, mostrando las transiciones que recorre.

La variable `automata` se va reemplazando en cada paso y al final contiene el autómata minimizado. Las cadenas del paso 7 se definen en el arreglo `entradas`:

```java
String[] entradas = {"a", "b", "ab", "ba", "abababa", "aab", "bbab"};
```

---

## Salida del programa

Salida con `archivos/auto2.json` (las líneas de la prueba de cadenas están resumidas):

```
Automata con estados:
q0 aceptador=false
q1 aceptador=false
q2 aceptador=true
Transiciones:
q0 --a--> q0
q0 --b--> q0
q0 --a--> q1
q1 --b--> q2

Es deterministico: false

Convertido a AFD:
Automata con estados:
<q0> aceptador=false
<q0, q1> aceptador=false
<q0, q2> aceptador=true
Transiciones:
<q0> --a--> <q0, q1>
<q0> --b--> <q0>
<q0, q1> --a--> <q0, q1>
<q0, q1> --b--> <q0, q2>
<q0, q2> --a--> <q0, q1>
<q0, q2> --b--> <q0>

Es deterministico: true

Minimizado:
Automata con estados:
[0] aceptador=false
[1] aceptador=false
[2] aceptador=true
Transiciones:
[0] --a--> [1]
[0] --b--> [0]
[1] --a--> [1]
[1] --b--> [2]
[2] --a--> [1]
[2] --b--> [0]

Es equivalente al original: true
Estados AFD: 3, estados minimizado: 3 -> se redujo o mantuvo: true

Automata minimizado guardado en: archivos/auto2_am.json

Transición: [0] --a--> [1]
La cadena "a" no es aceptada.
...
Transición: [0] --a--> [1]
Transición: [1] --b--> [2]
La cadena "ab" es aceptada.
...
```

En este caso el AFD ya era mínimo, así que la cantidad de estados se mantiene (3 → 3).

Cómo leer cada parte:

- **`Automata con estados`**: el autómata en ese momento (estados con su condición de aceptador, y transiciones como `origen --símbolo--> destino`).
- **`Es deterministico`**: resultado de `esDeterministico`.
- **`Convertido a AFD`**: solo aparece si la entrada era un AFND.
- **`Es equivalente al original`**: resultado de `sonEquivalentes`. Si da `false`, antes se imprime la cadena que no coincidió.
- **`Estados AFD ... se redujo o mantuvo`**: resultado de `seRedujo`.
- **`Transición: ...`**: cada paso que hace `transicionar` con las cadenas de ejemplo.

El archivo `archivos/auto2_am.json` generado:

```json
{
  "estadoInicial" : "[0]",
  "estados" : [
    { "nombre" : "[0]", "aceptador" : false },
    { "nombre" : "[1]", "aceptador" : false },
    { "nombre" : "[2]", "aceptador" : true }
  ],
  "transiciones" : [
    { "origen" : "[0]", "destino" : "[1]", "simbolo" : "a" },
    { "origen" : "[0]", "destino" : "[0]", "simbolo" : "b" },
    { "origen" : "[1]", "destino" : "[1]", "simbolo" : "a" },
    { "origen" : "[1]", "destino" : "[2]", "simbolo" : "b" },
    { "origen" : "[2]", "destino" : "[1]", "simbolo" : "a" },
    { "origen" : "[2]", "destino" : "[0]", "simbolo" : "b" }
  ],
  "alfabeto" : [ "a", "b" ]
}
```

(En el archivo real cada objeto ocupa varias líneas; acá está compactado para que se lea mejor.)

---

## Limitaciones

- **Símbolos de un carácter.** Las cadenas se recorren de a un carácter, así que un símbolo como `"ab"` en el alfabeto no funcionaría.
- **Sin transiciones lambda.** El conversor no maneja transiciones vacías (AFND-λ).
- **Cantidad de subconjuntos.** La conversión genera todos los subconjuntos (2ⁿ − 1) antes de eliminar los inalcanzables. Para los autómatas de la práctica no es un problema, pero con más de ~20 estados se vuelve lento.
- **La equivalencia se prueba, no se demuestra.** La validación usa cadenas al azar. Si todas coinciden, es muy probable que los lenguajes sean iguales, pero no es una demostración formal.
