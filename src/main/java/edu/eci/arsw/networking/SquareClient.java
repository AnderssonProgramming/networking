package edu.eci.arsw.networking;

import java.net.Socket;
import java.io.*;
import java.util.Scanner;

/**
 * Square Calculator Client
 * This client connects to the SquareServer and sends numbers to get their squares calculated.
 * 
 * Usage:
 * 1. Start the SquareServer first
 * 2. Run this client
 * 3. Enter numbers to get their squares
 * 4. Type "exit" or "quit" to disconnect
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class SquareClient {
    
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 35000;
    
    /**
     * Main method to start the client
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SquareClient client = new SquareClient();
        client.startClient();
    }
    
    /**
     * Starts the client and connects to the server
     */
    public void startClient() {
        System.out.println("=== SQUARE CALCULATOR CLIENT ===");
        System.out.println("Conectando al servidor en " + SERVER_HOST + ":" + SERVER_PORT);
        
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("Conectado al servidor exitosamente!");
            System.out.println("Ingrese números para calcular su cuadrado.");
            System.out.println("Escriba 'exit' o 'quit' para salir.");
            System.out.println();
            
            String userInput;
            while (true) {
                System.out.print("Ingrese un número: ");
                userInput = scanner.nextLine();
                
                // Check for exit commands
                if ("exit".equalsIgnoreCase(userInput.trim()) || "quit".equalsIgnoreCase(userInput.trim())) {
                    out.println(userInput);
                    System.out.println("Desconectando del servidor...");
                    break;
                }
                
                // Send the number to server
                out.println(userInput);
                
                // Read response from server
                String response = in.readLine();
                if (response != null) {
                    System.out.println("Servidor responde: " + response);
                } else {
                    System.out.println("Servidor desconectado");
                    break;
                }
                
                System.out.println();
            }
            
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            System.err.println("Asegúrese de que el servidor esté ejecutándose en " + SERVER_HOST + ":" + SERVER_PORT);
        }
    }
}
