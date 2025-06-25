package edu.eci.arsw.networking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.net.Socket;
import java.io.*;

/**
 * Test class for MathFunctionServer
 * Tests the mathematical function server functionality including:
 * - Trigonometric calculations (sin, cos, tan)
 * - Function switching commands
 * - Error handling for invalid inputs
 * - Multiple client connections
 */
class MathFunctionServerTest {    private MathFunctionServer server;
    private Thread serverThread;
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 35001;    @BeforeEach
    void setUp() {
        // Start server in a separate thread
        server = new MathFunctionServer();
        serverThread = new Thread(() -> server.startServer());
        serverThread.start();
        
        // Give server time to start - wait for it to be ready
        waitForServerToStart();
    }
    
    /**
     * Waits for the server to start by attempting connections
     */
    private void waitForServerToStart() {
        for (int i = 0; i < 50; i++) { // Increased attempts for reliability
            try (Socket testSocket = new Socket(SERVER_HOST, SERVER_PORT)) {
                // If connection succeeds, server is ready
                return;
            } catch (IOException e) {
                // Server not ready yet, yield to other threads
                Thread.yield();
            }
        }
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stopServer();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    @Test
    void testDefaultCosineFunction() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Test cos(0) = 1
            out.println("0");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("1.000000"), "cos(0) should be 1.000000");

            // Test cos(π/2) ≈ 0
            out.println("1.5707963267948966"); // π/2
            response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("0.000000"), "cos(π/2) should be approximately 0.000000");
        }
    }

    @Test
    void testFunctionSwitchingToSine() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Switch to sine function
            out.println("fun:sin");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("Función cambiada a: seno"), "Should confirm function change to sine");

            // Test sin(0) = 0
            out.println("0");
            response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("0.000000"), "sin(0) should be 0.000000");

            // Test sin(π/2) ≈ 1
            out.println("1.5707963267948966"); // π/2
            response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("1.000000"), "sin(π/2) should be approximately 1.000000");
        }
    }

    @Test
    void testFunctionSwitchingToTangent() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Switch to tangent function
            out.println("fun:tan");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("Función cambiada a: tangente"), "Should confirm function change to tangent");

            // Test tan(0) = 0
            out.println("0");
            response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("0.000000"), "tan(0) should be 0.000000");

            // Test tan(π/4) ≈ 1
            out.println("0.7853981633974483"); // π/4
            response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("1.000000"), "tan(π/4) should be approximately 1.000000");
        }
    }

    @Test
    void testInvalidFunctionCommand() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Try invalid function
            out.println("fun:invalid");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("Error"), "Should return error for invalid function");
            assertTrue(response.contains("no reconocida"), "Should mention function not recognized");
        }
    }

    @Test
    void testInvalidNumberInput() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send invalid number
            out.println("not_a_number");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("Error"), "Should return error for invalid number");
            assertTrue(response.contains("no es un número válido"), "Should mention invalid number");
        }
    }

    @Test
    void testEmptyInput() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send empty input
            out.println("");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("Error"), "Should return error for empty input");
            assertTrue(response.contains("Entrada vacía"), "Should mention empty input");
        }
    }

    @Test
    void testMultipleFunctionSwitches() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Start with default cosine, test cos(0) = 1
            out.println("0");
            String response = in.readLine();
            assertTrue(response.contains("1.000000"), "Default cos(0) should be 1.000000");

            // Switch to sine
            out.println("fun:sin");
            response = in.readLine();
            assertTrue(response.contains("seno"), "Should switch to sine");

            // Test sin(0) = 0
            out.println("0");
            response = in.readLine();
            assertTrue(response.contains("0.000000"), "sin(0) should be 0.000000");

            // Switch back to cosine
            out.println("fun:cos");
            response = in.readLine();
            assertTrue(response.contains("coseno"), "Should switch back to cosine");

            // Test cos(0) = 1 again
            out.println("0");
            response = in.readLine();
            assertTrue(response.contains("1.000000"), "cos(0) should be 1.000000 again");
        }
    }

    @Test
    void testNegativeNumbers() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Test cos(-π) = -1
            out.println("-3.141592653589793"); // -π
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("-1.000000"), "cos(-π) should be approximately -1.000000");
        }
    }

    @Test
    void testDecimalNumbers() throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Test with decimal input
            out.println("0.5");
            String response = in.readLine();
            assertNotNull(response);
            assertTrue(response.contains("0.877583"), "cos(0.5) should be approximately 0.877583");
        }
    }
}
