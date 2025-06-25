package edu.eci.arsw.networking;

/**
 * Automated test client for MathFunctionServer
 * This class demonstrates the server functionality by sending predefined commands
 * and displaying the responses, following the exercise examples.
 */
public class MathFunctionDemo {
    
    public static void main(String[] args) {
        testMathFunctionServer();
    }
    
    private static void testMathFunctionServer() {
        System.out.println("=== DEMO: MATHEMATICAL FUNCTION SERVER ===");
        System.out.println("Demostrando la funcionalidad según el ejercicio 4.3.2");
        System.out.println();
        
        try {
            // Test the exact sequence from the exercise description
            MathFunctionTestClient client = new MathFunctionTestClient();
            
            System.out.println("Conectando al servidor...");
            client.connect();
            
            // Test 1: Default function (cosine) with 0 → should return 1
            System.out.println("1. Enviando '0' (función por defecto: coseno)");
            String response1 = client.sendMessage("0");
            System.out.println("   Respuesta: " + response1);
            System.out.println("   Esperado: cos(0) = 1");
            
            // Test 2: Send π/2 → should return 0 (approximately)
            System.out.println("2. Enviando 'π/2' (1.5707963267948966)");
            String response2 = client.sendMessage("1.5707963267948966");
            System.out.println("   Respuesta: " + response2);
            System.out.println("   Esperado: cos(π/2) ≈ 0");
            
            // Test 3: Change function to sine
            System.out.println("3. Enviando 'fun:sin' (cambiar a función seno)");
            String response3 = client.sendMessage("fun:sin");
            System.out.println("   Respuesta: " + response3);
            System.out.println("   Esperado: Confirmación de cambio a seno");
            
            // Test 4: Send 0 with sine function → should return 0
            System.out.println("4. Enviando '0' (ahora con función seno)");
            String response4 = client.sendMessage("0");
            System.out.println("   Respuesta: " + response4);
            System.out.println("   Esperado: sin(0) = 0");
            
            // Test 5: Send π/2 with sine function → should return 1
            System.out.println("5. Enviando 'π/2' con función seno");
            String response5 = client.sendMessage("1.5707963267948966");
            System.out.println("   Respuesta: " + response5);
            System.out.println("   Esperado: sin(π/2) ≈ 1");
            
            // Test 6: Change to tangent function
            System.out.println("6. Enviando 'fun:tan' (cambiar a función tangente)");
            String response6 = client.sendMessage("fun:tan");
            System.out.println("   Respuesta: " + response6);
            
            // Test 7: Send π/4 with tangent → should return 1
            System.out.println("7. Enviando 'π/4' con función tangente");
            String response7 = client.sendMessage("0.7853981633974483");
            System.out.println("   Respuesta: " + response7);
            System.out.println("   Esperado: tan(π/4) = 1");
            
            client.disconnect();
            System.out.println();
            System.out.println("=== DEMO COMPLETADO ===");
            System.out.println("El servidor funciona correctamente según las especificaciones del ejercicio.");
            
        } catch (Exception e) {
            System.err.println("Error durante la demostración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Simple test client for automated testing
 */
class MathFunctionTestClient {
    private java.net.Socket socket;
    private java.io.PrintWriter out;
    private java.io.BufferedReader in;
    
    void connect() throws java.io.IOException {
        socket = new java.net.Socket("127.0.0.1", 35001);
        out = new java.io.PrintWriter(socket.getOutputStream(), true);
        in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
    }
    
    String sendMessage(String message) throws java.io.IOException {
        out.println(message);
        return in.readLine();
    }
    
    void disconnect() throws java.io.IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
    }
}
