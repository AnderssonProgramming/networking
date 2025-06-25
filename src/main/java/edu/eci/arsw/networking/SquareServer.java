package edu.eci.arsw.networking;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

/**
 * Exercise 4.3.1: Square Calculator Server
 * This server receives a number from a client and responds with the square of that number.
 * The server listens on a specific port and handles multiple client connections.
 * 
 * Protocol:
 * - Client sends: a number (as string)
 * - Server responds: "Respuesta: [square of the number]"
 * 
 * Based on the networking tutorial by Luis Daniel Benavides Navarro
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class SquareServer {
    
    private static final int SERVER_PORT = 35000;
    private static final String RESPONSE_PREFIX = "Respuesta: ";
    private boolean isRunning = true;
    
    /**
     * Main method to start the server
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SquareServer server = new SquareServer();
        server.startServer();
    }
    
    /**
     * Starts the server and listens for client connections
     */
    public void startServer() {
        System.out.println("=== SQUARE CALCULATOR SERVER ===");
        System.out.println("Servidor iniciado en puerto: " + SERVER_PORT);
        System.out.println("Esperando conexiones de clientes...");
        System.out.println("Presione Ctrl+C para detener el servidor");
        System.out.println();
        
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            
            while (isRunning) {
                try {
                    // Wait for client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nueva conexión desde: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Handle client in a separate thread for concurrent connections
                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.start();
                    
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error aceptando conexión del cliente: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error iniciando el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles communication with a single client
     * @param clientSocket The socket connection to the client
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Cliente conectado desde: " + clientAddress);
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensaje recibido de " + clientAddress + ": " + inputLine);
                
                // Process the input and calculate square
                String response = processInput(inputLine);
                
                // Send response to client
                out.println(response);
                System.out.println("Respuesta enviada a " + clientAddress + ": " + response);
                
                // Break if client sends "exit" or "quit"
                if ("exit".equalsIgnoreCase(inputLine.trim()) || "quit".equalsIgnoreCase(inputLine.trim())) {
                    System.out.println("Cliente " + clientAddress + " solicitó desconexión");
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error en comunicación con cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Conexión cerrada con cliente: " + clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.err.println("Error cerrando socket del cliente: " + e.getMessage());
            }
        }
    }
    
    /**
     * Processes the input from the client and calculates the square
     * @param input The input string from the client
     * @return The response string with the square calculation or error message
     */
    private String processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return RESPONSE_PREFIX + "Error - Entrada vacía";
        }
        
        try {
            // Parse the input as a number
            double number = Double.parseDouble(input.trim());
            
            // Calculate the square
            double square = number * number;
            
            // Format response based on whether the result is a whole number
            if (square == Math.floor(square)) {
                return RESPONSE_PREFIX + String.format("%.0f", square);
            } else {
                return RESPONSE_PREFIX + String.format("%.6f", square);
            }
            
        } catch (NumberFormatException e) {
            return RESPONSE_PREFIX + "Error - '" + input.trim() + "' no es un número válido";
        }
    }
    
    /**
     * Stops the server gracefully
     */
    public void stopServer() {
        isRunning = false;
        System.out.println("Deteniendo servidor...");
    }
}
