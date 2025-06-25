#!/bin/bash

# EJERCICIO 4.3.1 - Square Calculator Demo Script
# This script demonstrates the socket server functionality

echo "=== EJERCICIO 4.3.1 DEMONSTRATION ==="
echo "Square Calculator Socket Server"
echo ""

echo "1. Compiling the project..."
mvn compile

echo ""
echo "2. Running comprehensive tests..."
mvn test -Dtest=SquareServerTest

echo ""
echo "3. Demo completed successfully!"
echo ""
echo "To manually test the server:"
echo "   Terminal 1: java -cp target/classes edu.eci.arsw.networking.SquareServer"
echo "   Terminal 2: java -cp target/classes edu.eci.arsw.networking.SquareClient"
echo ""
echo "The server will:"
echo "   - Listen on port 35000"
echo "   - Accept multiple concurrent clients"
echo "   - Calculate squares of input numbers"
echo "   - Handle errors gracefully"
echo ""
echo "Test examples:"
echo "   Input: 5      → Output: Respuesta: 25"
echo "   Input: -3     → Output: Respuesta: 9"
echo "   Input: 2.5    → Output: Respuesta: 6.250000"
echo "   Input: abc    → Output: Respuesta: Error - 'abc' no es un número válido"
