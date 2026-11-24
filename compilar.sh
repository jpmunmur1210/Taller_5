#!/bin/bash
# Compila el proyecto (requiere JDK 11 o superior)
mkdir -p out
javac -encoding UTF-8 -d out src/*.java && echo "Compilado en ./out"
