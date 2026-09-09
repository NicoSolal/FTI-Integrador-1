# Comando por defecto al escribir solo 'make'
all: clean compile run

# Compilar el proyecto
compile:
	mvn compile

# Ejecutar el Main
run:
	mvn exec:java

# Limpiar archivos compilados viejos (target/)
clean:
	mvn clean

# Recompilar de cero y ejecutar
rebuild: clean compile run