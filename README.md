# Taller 5 - Ordenamiento de documentos (Java / Swing)

Incluye el ejercicio 1 (Burbuja y Quicksort, dos criterios, tiempo de ejecución)
y el ejercicio 2 (Inserción, Selección y Mezcla).

## Estructura
- `src/Documento.java`: clase con atributos/métodos estáticos y los 5 algoritmos.
- `src/FrmOrdenamiento.java`: interfaz gráfica (botones por algoritmo, criterio, tiempo).
- `src/Util.java`, `src/Archivo.java`: cronómetro, redimensionar arreglos, lectura del CSV.
- `src/Benchmark.java`: prueba por consola que verifica el orden y muestra tiempos.
- `src/Datos/Datos.csv`: datos (99.999 registros).

## Ejecutar (requiere JDK 11+)
- Linux/Mac: `./ejecutar.sh`   - Windows: `ejecutar.bat`
- Prueba por consola: `./benchmark.sh 10000` (o `benchmark.bat 10000`)
- En NetBeans/IntelliJ: importar la carpeta `src` y ejecutar `FrmOrdenamiento`.

Ejecutar siempre desde la carpeta del proyecto; si no encuentra el CSV, la aplicación
pide seleccionarlo.

## Notas
- Burbuja, Selección e Inserción con los 99.999 registros pueden tardar minutos
  (son O(n²)). Use "Registros a cargar" para probar con menos datos.
- Botón "Comparar todos": ejecuta los 5 algoritmos sobre la misma lista y muestra los tiempos.
- Quicksort usa el elemento central como pivote y un hilo con pila grande para evitar
  desbordamiento con listas ya ordenadas.
- Resultados con 10.000 registros (Nombre-Documento): Burbuja 0,78 s, Selección 0,37 s,
  Inserción 0,20 s, Rápido 0,016 s, Mezcla 0,012 s. Todas verificadas como ordenadas.
