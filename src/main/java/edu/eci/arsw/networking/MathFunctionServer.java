package edu.eci.arsw.networking;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

/**
 * Exercise 4.3.2: Mathematical Function Server
 * This server receives numbers and applies trigonometric functions (sine, cosine, tangent).
 * The server can dynamically change the operation based on "fun:" commands.
 * 
 * Protocol:
 * - Client sends: a number (as string) or "fun:[operation]"
 * - Server responds: "Respuesta: [result of function applied to number]"
 * - Supported functions: sin, cos, tan
 * - Default function: cos (cosine)
 * 
 * Function switching commands:
 * - "fun:sin" - Switch to sine function
 * - "fun:cos" - Switch to cosine function  
 * - "fun:tan" - Switch to tangent function
 * 
 * Examples:
 * - Input: "0" (default cos) → Output: "Respuesta: 1.000000"
 * - Input: "fun:sin" → Output: "Respuesta: Función cambiada a: sin"
 * - Input: "0" (now sin) → Output: "Respuesta: 0.000000"
 * 
 * Based on the networking tutorial by Luis Daniel Benavides Navarro
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class MathFunctionServer {
    
    private static final int SERVER_PORT = 35001;
    private static final String RESPONSE_PREFIX = "Respuesta: ";
    private static final String FUNCTION_COMMAND_PREFIX = "fun:";
    
    /**
     * Enumeration of supported mathematical functions
     */
    public enum MathFunction {
        SIN("sin", "seno"),
        COS("cos", "coseno"), 
        TAN("tan", "tangente");
        
        private final String command;
        private final String displayName;
        
        MathFunction(String command, String displayName) {
            this.command = command;
            this.displayName = displayName;
        }
        
        public String getCommand() { return command; }
        public String getDisplayName() { return displayName; }
        
        public static MathFunction fromCommand(String command) {
            for (MathFunction func : values()) {
                if (func.command.equalsIgnoreCase(command)) {
                    return func;
                }
            }
            return null;
        }
    }
      private volatile boolean isRunning = false;
    private ServerSocket serverSocket;
    
    /**
     * Main method to start the server
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        MathFunctionServer server = new MathFunctionServer();
        server.startServer();
    }
    
    /**
     * Starts the server and listens for client connections
     */
    public void startServer() {
        if (isRunning) {
            System.out.println("El servidor ya está ejecutándose");
            return;
        }
        
        System.out.println("=== MATHEMATICAL FUNCTION SERVER ===");
        System.out.println("Servidor iniciado en puerto: " + SERVER_PORT);
        System.out.println("Función por defecto: coseno (cos)");
        System.out.println("Funciones disponibles: sin, cos, tan");
        System.out.println("Comando para cambiar función: fun:[función]");
        System.out.println("Esperando conexiones de clientes...");
        System.out.println("Presione Ctrl+C para detener el servidor");
        System.out.println();
        
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            isRunning = true;
            
            while (isRunning) {
                try {
                    // Wait for client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nueva conexión desde: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Handle client in a separate thread for concurrent connections
                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.setDaemon(true);
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
        } finally {
            cleanup();
        }
    }
    
    /**
     * Handles communication with a single client
     * Each client maintains its own function state
     * @param clientSocket The socket connection to the client
     */
    private void handleClient(Socket clientSocket) {
        // Each client starts with cosine as the default function
        MathFunction currentFunction = MathFunction.COS;
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Cliente conectado desde: " + clientAddress);
            System.out.println("Función inicial para " + clientAddress + ": " + currentFunction.getDisplayName());
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensaje recibido de " + clientAddress + ": " + inputLine);
                
                // Process the input
                ProcessResult result = processInput(inputLine, currentFunction);
                
                // Update current function if it was changed
                if (result.newFunction != null) {
                    currentFunction = result.newFunction;
                    System.out.println("Función cambiada para " + clientAddress + ": " + currentFunction.getDisplayName());
                }
                
                // Send response to client
                out.println(result.response);
                System.out.println("Respuesta enviada a " + clientAddress + ": " + result.response);
                
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
     * Result class to hold the processing result and any function change
     */
    private static class ProcessResult {
        final String response;
        final MathFunction newFunction;
        
        ProcessResult(String response, MathFunction newFunction) {
            this.response = response;
            this.newFunction = newFunction;
        }
    }
    
    /**
     * Processes the input from the client
     * @param input The input string from the client
     * @param currentFunction The current mathematical function being used
     * @return ProcessResult containing the response and any function change
     */
    private ProcessResult processInput(String input, MathFunction currentFunction) {
        if (input == null || input.trim().isEmpty()) {
            return new ProcessResult(RESPONSE_PREFIX + "Error - Entrada vacía", null);
        }
        
        String trimmedInput = input.trim();
        
        // Check if it's a function change command
        if (trimmedInput.toLowerCase().startsWith(FUNCTION_COMMAND_PREFIX)) {
            return processFunctionCommand(trimmedInput);
        }
        
        // Otherwise, treat it as a number and apply the current function
        return processNumberInput(trimmedInput, currentFunction);
    }
    
    /**
     * Processes a function change command
     * @param command The function command (e.g., "fun:sin")
     * @return ProcessResult with confirmation message and new function
     */
    private ProcessResult processFunctionCommand(String command) {
        String functionName = command.substring(FUNCTION_COMMAND_PREFIX.length()).toLowerCase();
        
        MathFunction newFunction = MathFunction.fromCommand(functionName);
        
        if (newFunction != null) {
            String response = RESPONSE_PREFIX + "Función cambiada a: " + newFunction.getDisplayName();
            return new ProcessResult(response, newFunction);
        } else {
            String response = RESPONSE_PREFIX + "Error - Función '" + functionName + 
                            "' no reconocida. Funciones disponibles: sin, cos, tan";
            return new ProcessResult(response, null);
        }
    }
    
    /**
     * Processes a numeric input and applies the mathematical function
     * @param input The numeric input as string
     * @param function The mathematical function to apply
     * @return ProcessResult with the calculation result
     */
    private ProcessResult processNumberInput(String input, MathFunction function) {
        try {
            // Parse the input as a number
            double number = Double.parseDouble(input);
            
            // Apply the mathematical function
            double result = applyFunction(number, function);
            
            // Format the response
            String response;
            if (Double.isNaN(result)) {
                response = RESPONSE_PREFIX + "Error - Resultado indefinido (NaN)";
            } else if (Double.isInfinite(result)) {
                response = RESPONSE_PREFIX + "Error - Resultado infinito";
            } else {
                // Format with 6 decimal places for precision
                response = RESPONSE_PREFIX + String.format("%.6f", result);
            }
            
            return new ProcessResult(response, null);
            
        } catch (NumberFormatException e) {
            String response = RESPONSE_PREFIX + "Error - '" + input + "' no es un número válido";
            return new ProcessResult(response, null);
        }
    }
    
    /**
     * Applies the specified mathematical function to the input number
     * @param number The input number (in radians for trigonometric functions)
     * @param function The mathematical function to apply
     * @return The result of applying the function
     */
    private double applyFunction(double number, MathFunction function) {
        switch (function) {
            case SIN:
                return Math.sin(number);
            case COS:
                return Math.cos(number);
            case TAN:
                return Math.tan(number);
            default:
                throw new IllegalArgumentException("Función no soportada: " + function);
        }
    }
      /**
     * Stops the server gracefully
     */
    public void stopServer() {
        isRunning = false;
        System.out.println("Deteniendo servidor...");
        cleanup();
    }
    
    /**
     * Cleanup method to close the server socket
     */
    private void cleanup() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Socket del servidor cerrado");
            } catch (IOException e) {
                System.err.println("Error cerrando socket del servidor: " + e.getMessage());
            }
        }
    }
}
