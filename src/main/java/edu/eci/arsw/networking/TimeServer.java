package edu.eci.arsw.networking;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Exercise 5.2.1: Time Server using UDP Datagrams
 * This server responds with the current server time when it receives a request
 * using UDP protocol for non-guaranteed message delivery.
 * 
 * Features:
 * - UDP DatagramSocket communication
 * - Current time response service
 * - Handles multiple client requests
 * - Robust error handling
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class TimeServer {
    
    private static final int SERVER_PORT = 45000;
    private static final int BUFFER_SIZE = 256;
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private DatagramSocket socket;
    private boolean isRunning = false;
    private SimpleDateFormat dateFormatter;
    
    /**
     * Constructor initializes the date formatter
     */
    public TimeServer() {
        this.dateFormatter = new SimpleDateFormat(TIME_FORMAT);
    }
    
    /**
     * Main method to start the time server
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        TimeServer server = new TimeServer();
        server.startServer();
    }
    
    /**
     * Starts the UDP time server
     */
    public void startServer() {
        System.out.println("=== UDP TIME SERVER ===");
        System.out.println("Servidor de tiempo iniciado en puerto: " + SERVER_PORT);
        System.out.println("Esperando solicitudes de tiempo...");
        System.out.println("Presione Ctrl+C para detener el servidor");
        System.out.println();
        
        try {
            // Create DatagramSocket bound to server port
            socket = new DatagramSocket(SERVER_PORT);
            isRunning = true;
            
            // Buffer for receiving requests
            byte[] buffer = new byte[BUFFER_SIZE];
            
            while (isRunning) {
                try {
                    // Create packet to receive client request
                    DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                    
                    // Wait for client request
                    socket.receive(requestPacket);
                    
                    // Get client information
                    InetAddress clientAddress = requestPacket.getAddress();
                    int clientPort = requestPacket.getPort();
                    String requestMessage = new String(requestPacket.getData(), 0, requestPacket.getLength());
                    
                    System.out.println("Solicitud recibida de: " + clientAddress.getHostAddress() + 
                                     ":" + clientPort + " - Mensaje: '" + requestMessage.trim() + "'");
                    
                    // Generate current time response
                    String currentTime = getCurrentTimeString();
                    System.out.println("Enviando hora actual: " + currentTime);
                    
                    // Create response packet
                    byte[] responseData = currentTime.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                        responseData, 
                        responseData.length, 
                        clientAddress, 
                        clientPort
                    );
                    
                    // Send response to client
                    socket.send(responsePacket);
                    System.out.println("Respuesta enviada exitosamente");
                    System.out.println("---");
                    
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error procesando solicitud del cliente: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error iniciando servidor de tiempo: " + e.getMessage());
        } finally {
            stopServer();
        }
    }
    
    /**
     * Stops the UDP time server
     */
    public void stopServer() {
        isRunning = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Servidor de tiempo detenido");
        }
    }
    
    /**
     * Gets the current server time as a formatted string
     * @return Current time formatted as string
     */
    private String getCurrentTimeString() {
        return dateFormatter.format(new Date());
    }
    
    /**
     * Checks if the server is currently running
     * @return true if server is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Gets the server port
     * @return Server port number
     */
    public int getServerPort() {
        return SERVER_PORT;
    }
}
