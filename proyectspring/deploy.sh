#!/bin/bash
# Script para instalar dependencias y compilar el proyecto Spring Boot

set -e

# Verificar Java
if ! command -v java &> /dev/null; then
  echo "Java no está instalado. Instala Java 17 o superior antes de continuar."
  exit 1
fi

# Verificar Maven
if ! command -v mvn &> /dev/null && [ ! -f "./mvnw" ]; then
  echo "Maven no está instalado y no se encontró mvnw. Instala Maven 3.6+ o usa el wrapper incluido."
  exit 1
fi

# Instalar dependencias y compilar
if [ -f "./mvnw" ]; then
  ./mvnw clean install
else
  mvn clean install
fi

echo "Dependencias instaladas y proyecto compilado correctamente."
