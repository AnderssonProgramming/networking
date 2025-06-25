package edu.eci.arsw.networking;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Exercise 5.2.1: Time Client using UDP Datagrams
 * This client requests the current time from a TimeServer every 5 seconds.
 * It handles server disconnections gracefully and continues working when
 * the server comes back online.
 * 
 * Features:
 * - UDP DatagramSocket communication
 * - Automatic time updates every 5 seconds
 * - Handles server disconnections
 * - Maintains last known time when server is offline
 * - Graceful reconnection when server comes back
 * - Interactive stop functionality
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class TimeClient {
    
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 45000;
    private static final int SOCKET_TIMEOUT = 3000; // 3 seconds timeout
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds between updates
    private static final String REQUEST_MESSAGE = "GET_TIME";
    private static final int BUFFER_SIZE = 256;
    
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private boolean isRunning = false;
    private String lastKnownTime = "No hay datos de tiempo disponibles";
    private SimpleDateFormat localTimeFormatter;
    
    /**
     * Constructor initializes the client
     */
    public TimeClient() {
        this.localTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * Main method to start the time client
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        TimeClient client = new TimeClient();
        client.startClient();
    }
    
    /**
     * Starts the UDP time client
     */
    public void startClient() {
        System.out.println("=== UDP TIME CLIENT ===");
        System.out.println("Cliente de tiempo iniciado");
        System.out.println("Servidor: " + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println("Actualizando cada " + (UPDATE_INTERVAL/1000) + " segundos");
        System.out.println("Timeout de conexión: " + (SOCKET_TIMEOUT/1000) + " segundos");
        System.out.println("Presione 'q' + Enter para salir");
        System.out.println();
          try (DatagramSocket clientSocket = new DatagramSocket()) {
            // Initialize socket and server address
            this.socket = clientSocket;
            clientSocket.setSoTimeout(SOCKET_TIMEOUT);
            serverAddress = InetAddress.getByName(SERVER_HOST);
            isRunning = true;
            
            // Start input monitoring thread for graceful shutdown
            startInputMonitorThread();
            
            // Main time update loop
            while (isRunning) {
                try {
                    // Request time from server
                    String serverTime = requestTimeFromServer();
                    
                    if (serverTime != null) {
                        lastKnownTime = serverTime;
                        System.out.println("✅ [" + getCurrentLocalTime() + "] Tiempo del servidor: " + serverTime);
                    } else {
                        System.out.println("❌ [" + getCurrentLocalTime() + "] Servidor no disponible - Última hora conocida: " + lastKnownTime);
                    }
                    
                } catch (Exception e) {
                    System.out.println("❌ [" + getCurrentLocalTime() + "] Error de conexión - Última hora conocida: " + lastKnownTime);
                }
                
                // Wait for next update
                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Cliente interrumpido");
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error inicializando cliente de tiempo: " + e.getMessage());
        } finally {
            stopClient();
        }
    }
    
    /**
     * Requests time from the server using UDP
     * @return Server time string or null if request failed
     */
    private String requestTimeFromServer() {
        try {
            // Create request packet
            byte[] requestData = REQUEST_MESSAGE.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(
                requestData, 
                requestData.length, 
                serverAddress, 
                SERVER_PORT
            );
            
            // Send request to server
            socket.send(requestPacket);
            
            // Prepare to receive response
            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            
            // Receive response (with timeout)
            socket.receive(responsePacket);
            
            // Extract server time from response
            String serverTime = new String(responsePacket.getData(), 0, responsePacket.getLength());
            return serverTime.trim();        } catch (SocketTimeoutException e) {
            // Server didn't respond within timeout
            return null;
        } catch (IOException e) {
            // Network error occurred
            return null;
        }
    }
    
    /**
     * Starts a thread to monitor user input for graceful shutdown
     */
    private void startInputMonitorThread() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (isRunning) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    if ("q".equals(input) || "quit".equals(input) || "exit".equals(input)) {
                        System.out.println("Deteniendo cliente...");
                        isRunning = false;
                        break;
                    }
                }                try {
                    Thread.sleep(100); // Small delay to prevent busy waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }
    
    /**
     * Gets the current local time as a formatted string
     * @return Current local time formatted as string
     */
    private String getCurrentLocalTime() {
        return localTimeFormatter.format(new Date());
    }
    
    /**
     * Stops the UDP time client
     */
    public void stopClient() {
        isRunning = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Cliente de tiempo detenido");
        }
    }
    
    /**
     * Checks if the client is currently running
     * @return true if client is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Gets the last known time from the server
     * @return Last known server time
     */
    public String getLastKnownTime() {
        return lastKnownTime;
    }
    
    /**
     * Gets the server host
     * @return Server host name
     */
    public String getServerHost() {
        return SERVER_HOST;
    }
    
    /**
     * Gets the server port
     * @return Server port number
     */
    public int getServerPort() {
        return SERVER_PORT;
    }
}
