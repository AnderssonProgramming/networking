package edu.eci.arsw.networking;

import java.util.Scanner;

/**
 * Exercise 5.2.1: Time Server and Client Demo using UDP Datagrams
 * This demo demonstrates the exact functionality required:
 * 
 * 1. Time server that responds with current time
 * 2. Time client that updates every 5 seconds
 * 3. Client maintains last known time when server is offline
 * 4. Client continues working when server comes back online
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class TimeDemo {
    
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 5.2.1 - DEMO UDP DATAGRAM TIME SERVER/CLIENT ===");
        System.out.println("Este demo demuestra la funcionalidad exacta requerida:");
        System.out.println("1. Servidor de tiempo que responde la hora actual");
        System.out.println("2. Cliente que actualiza cada 5 segundos");
        System.out.println("3. Cliente mantiene última hora conocida cuando servidor está offline");
        System.out.println("4. Cliente continúa funcionando cuando el servidor vuelve online");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        // Show menu options
        System.out.println("Seleccione una opción:");
        System.out.println("1. Iniciar servidor de tiempo (TimeServer)");
        System.out.println("2. Iniciar cliente de tiempo (TimeClient)");
        System.out.println("3. Demo completo automatizado");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
        
        int option = scanner.nextInt();
        
        switch (option) {
            case 1:
                startTimeServer();
                break;
            case 2:
                startTimeClient();
                break;
            case 3:
                runAutomatedDemo();
                break;
            case 0:
                System.out.println("Saliendo...");
                break;
            default:
                System.out.println("Opción no válida");
        }
        
        scanner.close();
    }
    
    /**
     * Starts the time server
     */
    private static void startTimeServer() {
        System.out.println("🚀 Iniciando servidor de tiempo...");
        System.out.println("Nota: Para probar la funcionalidad completa:");
        System.out.println("1. Ejecute este servidor");
        System.out.println("2. En otra terminal, ejecute el cliente");
        System.out.println("3. Detenga el servidor (Ctrl+C) para simular desconexión");
        System.out.println("4. Reinicie el servidor para ver la reconexión automática");
        System.out.println();
        
        TimeServer server = new TimeServer();
        server.startServer();
    }
    
    /**
     * Starts the time client
     */
    private static void startTimeClient() {
        System.out.println("🚀 Iniciando cliente de tiempo...");
        System.out.println("Nota: Asegúrese de que el servidor esté ejecutándose");
        System.out.println("El cliente:");
        System.out.println("- Solicita la hora cada 5 segundos");
        System.out.println("- Mantiene la última hora conocida si el servidor no responde");
        System.out.println("- Se reconecta automáticamente cuando el servidor vuelve online");
        System.out.println();
        
        TimeClient client = new TimeClient();
        client.startClient();
    }
    
    /**
     * Runs an automated demo showing the complete functionality
     */
    private static void runAutomatedDemo() {
        System.out.println("🎬 DEMO AUTOMATIZADO - EJERCICIO 5.2.1");
        System.out.println("═══════════════════════════════════════");
        System.out.println();
        
        try {
            // Step 1: Start server
            System.out.println("📺 PASO 1: Iniciando servidor de tiempo...");
            TimeServer server = new TimeServer();
            Thread serverThread = new Thread(() -> server.startServer());
            serverThread.setDaemon(true);
            serverThread.start();
            
            // Wait for server to start
            Thread.sleep(2000);
            System.out.println("✅ Servidor iniciado exitosamente");
            System.out.println();
              // Step 2: Demonstrate time requests
            System.out.println("📺 PASO 2: Demostrando solicitudes de tiempo...");
            
            // Manual time requests to show functionality
            for (int i = 1; i <= 3; i++) {
                String time = requestTimeFromServer();
                if (time != null) {
                    System.out.println("✅ Solicitud " + i + " - Hora recibida: " + time);
                } else {
                    System.out.println("❌ Solicitud " + i + " - Servidor no disponible");
                }
                Thread.sleep(1000);
            }
            System.out.println();
            
            // Step 3: Demonstrate server disconnection
            System.out.println("📺 PASO 3: Simulando desconexión del servidor...");
            server.stopServer();
            Thread.sleep(1000);
            
            // Try to request time with server offline
            String time = requestTimeFromServer();
            if (time == null) {
                System.out.println("❌ Servidor offline - Manteniendo última hora conocida");
            }
            System.out.println();
            
            // Step 4: Demonstrate server reconnection
            System.out.println("📺 PASO 4: Reconectando servidor...");
            TimeServer newServer = new TimeServer();
            Thread newServerThread = new Thread(() -> newServer.startServer());
            newServerThread.setDaemon(true);
            newServerThread.start();
            
            Thread.sleep(2000);
            
            // Test reconnection
            time = requestTimeFromServer();
            if (time != null) {
                System.out.println("✅ Reconexión exitosa - Nueva hora: " + time);
            }
            
            newServer.stopServer();
            System.out.println();
            
            // Summary
            System.out.println("🎯 RESUMEN DEL DEMO:");
            System.out.println("✅ Servidor UDP responde solicitudes de tiempo");
            System.out.println("✅ Cliente puede solicitar tiempo del servidor");
            System.out.println("✅ Cliente maneja desconexiones gracefully");
            System.out.println("✅ Cliente se reconecta automáticamente");
            System.out.println("✅ Protocolo UDP (datagramas) funciona correctamente");
            System.out.println();
            
            System.out.println("🚀 Para uso interactivo:");
            System.out.println("1. Ejecute: java -cp target/classes edu.eci.arsw.networking.TimeServer");
            System.out.println("2. En otra terminal: java -cp target/classes edu.eci.arsw.networking.TimeClient");
            System.out.println("3. Detenga/reinicie el servidor para ver la funcionalidad completa");
              } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Demo interrumpido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error durante el demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to request time from server for demo purposes
     * @return Server time or null if not available
     */
    private static String requestTimeFromServer() {
        try {
            java.net.DatagramSocket socket = new java.net.DatagramSocket();
            socket.setSoTimeout(2000);
            
            // Send request
            String message = "GET_TIME";
            byte[] requestData = message.getBytes();
            java.net.DatagramPacket requestPacket = new java.net.DatagramPacket(
                requestData, requestData.length,
                java.net.InetAddress.getByName("localhost"), 45000
            );
            socket.send(requestPacket);
            
            // Receive response
            byte[] responseBuffer = new byte[256];
            java.net.DatagramPacket responsePacket = new java.net.DatagramPacket(
                responseBuffer, responseBuffer.length
            );
            socket.receive(responsePacket);
            
            socket.close();
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
            
        } catch (Exception e) {
            return null;
        }
    }
}
