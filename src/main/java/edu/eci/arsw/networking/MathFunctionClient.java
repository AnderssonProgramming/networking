package edu.eci.arsw.networking;

import java.net.Socket;
import java.io.*;
import java.util.Scanner;

/**
 * Mathematical Function Client
 * Client application to test the MathFunctionServer.
 * Allows users to send numbers and function change commands to the server.
 * 
 * Usage:
 * - Enter numbers to calculate with current function
 * - Enter "fun:[function]" to change function (sin, cos, tan)
 * - Enter "exit" or "quit" to disconnect
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class MathFunctionClient {
    
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 35001;
    
    /**
     * Main method to start the client
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        MathFunctionClient client = new MathFunctionClient();
        client.startClient();
    }
    
    /**
     * Starts the client and connects to the server
     */
    public void startClient() {
        System.out.println("=== MATHEMATICAL FUNCTION CLIENT ===");
        System.out.println("Conectando al servidor " + SERVER_HOST + ":" + SERVER_PORT);
        
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("¡Conectado al servidor!");
            System.out.println();
            printUsageInstructions();
            
            String userInput;
            while (true) {
                System.out.print("Ingrese número o comando: ");
                userInput = scanner.nextLine().trim();
                
                // Check for exit commands
                if ("exit".equalsIgnoreCase(userInput) || "quit".equalsIgnoreCase(userInput)) {
                    out.println(userInput);
                    System.out.println("Desconectando del servidor...");
                    break;
                }
                
                // Send input to server
                out.println(userInput);
                
                // Read and display server response
                String response = in.readLine();
                if (response != null) {
                    System.out.println("Servidor: " + response);
                } else {
                    System.out.println("Conexión perdida con el servidor");
                    break;
                }
                
                System.out.println();
            }
            
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            System.err.println("Asegúrese de que el servidor esté ejecutándose en " + SERVER_HOST + ":" + SERVER_PORT);
        }
    }
    
    /**
     * Prints usage instructions for the user
     */
    private void printUsageInstructions() {
        System.out.println("=== INSTRUCCIONES DE USO ===");
        System.out.println("• Ingrese un número para calcular con la función actual");
        System.out.println("• Ingrese 'fun:sin' para cambiar a función seno");
        System.out.println("• Ingrese 'fun:cos' para cambiar a función coseno");
        System.out.println("• Ingrese 'fun:tan' para cambiar a función tangente");
        System.out.println("• Ingrese 'exit' o 'quit' para salir");
        System.out.println("• Los ángulos deben estar en radianes");
        System.out.println("• Función por defecto: coseno");
        System.out.println();
        System.out.println("=== EJEMPLOS ===");
        System.out.println("• 0        → cos(0) = 1.000000");
        System.out.println("• 1.5708   → cos(π/2) ≈ 0.000000");
        System.out.println("• fun:sin  → Cambia a función seno");
        System.out.println("• 0        → sin(0) = 0.000000");
        System.out.println("• 1.5708   → sin(π/2) ≈ 1.000000");
        System.out.println();
    }
}
