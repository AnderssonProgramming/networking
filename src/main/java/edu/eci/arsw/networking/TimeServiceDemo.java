package edu.eci.arsw.networking;

/**
 * Exercise 5.2.1: Time Service Demo using UDP Datagrams
 * This demo class shows how to use the TimeServer and TimeClient
 * for UDP-based time synchronization service.
 * 
 * Features demonstrated:
 * - UDP time server/client communication
 * - Automatic time updates every 5 seconds
 * - Server disconnection handling
 * - Graceful reconnection when server comes back
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class TimeServiceDemo {
    
    /**
     * Main method to demonstrate the time service
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== DEMO: EJERCICIO 5.2.1 - SERVICIO DE TIEMPO UDP ===");
        System.out.println();
        System.out.println("Este demo muestra el funcionamiento del servicio de tiempo usando datagramas UDP:");
        System.out.println("1. TimeServer - Servidor que responde con la hora actual");
        System.out.println("2. TimeClient - Cliente que solicita la hora cada 5 segundos");
        System.out.println();
        System.out.println("INSTRUCCIONES PARA LA PRUEBA:");
        System.out.println("------------------------------");
        System.out.println("1. Ejecute primero el TimeServer en una terminal:");
        System.out.println("   mvn exec:java -Dexec.mainClass=\"edu.eci.arsw.networking.TimeServer\"");
        System.out.println();
        System.out.println("2. Ejecute el TimeClient en otra terminal:");
        System.out.println("   mvn exec:java -Dexec.mainClass=\"edu.eci.arsw.networking.TimeClient\"");
        System.out.println();
        System.out.println("3. Observe cómo el cliente recibe actualizaciones de tiempo cada 5 segundos");
        System.out.println();
        System.out.println("4. Para probar la resistencia a desconexiones:");
        System.out.println("   - Detenga el servidor (Ctrl+C)");
        System.out.println("   - Observe cómo el cliente mantiene la última hora conocida");
        System.out.println("   - Reinicie el servidor");
        System.out.println("   - Observe cómo el cliente se reconecta automáticamente");
        System.out.println();
        System.out.println("5. Para detener el cliente, escriba 'q' y presione Enter");
        System.out.println();
        System.out.println("CARACTERÍSTICAS IMPLEMENTADAS:");
        System.out.println("------------------------------");
        System.out.println("✅ Comunicación UDP (sin garantía de entrega)");
        System.out.println("✅ Actualizaciones automáticas cada 5 segundos");
        System.out.println("✅ Manejo de desconexiones del servidor");
        System.out.println("✅ Mantenimiento de última hora conocida");
        System.out.println("✅ Reconexión automática cuando el servidor vuelve");
        System.out.println("✅ Timeouts configurables");
        System.out.println("✅ Interfaz interactiva para detener el cliente");
        System.out.println();
        System.out.println("PROTOCOLO UDP vs TCP:");
        System.out.println("---------------------");
        System.out.println("• UDP es más rápido pero no garantiza la entrega");
        System.out.println("• Ideal para servicios como actualizaciones de tiempo");
        System.out.println("• No mantiene conexión persistente");
        System.out.println("• Menor overhead de red");
        System.out.println();
        System.out.println("¡Ejecute los comandos anteriores para ver el demo en acción!");
    }
}
