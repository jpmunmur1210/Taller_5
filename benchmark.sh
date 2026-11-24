#!/bin/bash
# Prueba por consola de los 5 algoritmos. Uso: ./benchmark.sh [maxRegistros]
./compilar.sh && java -cp out Benchmark src/Datos/Datos.csv ${1:-10000}
