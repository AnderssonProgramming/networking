// package edu.eci.arsw.networking;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.AfterEach;
// import static org.junit.jupiter.api.Assertions.*;
// import java.net.Socket;
// import java.net.InetSocketAddress;
// import java.io.*;

// /**
//  * Test class for MathFunctionServer
//  * Tests the mathematical function server functionality including:
//  * - Trigonometric calculations (sin, cos, tan)
//  * - Function switching commands
//  * - Error handling for invalid inputs
//  * - Multiple client connections
//  */
// class MathFunctionServerTest {

//     private static final String SERVER_HOST = "127.0.0.1";
//     private static final int SERVER_PORT = 35001;
    
//     private MathFunctionServer server;
//     private Thread serverThread;

//     @BeforeEach
//     void setUp() throws InterruptedException {
//         // Ensure port is free before starting
//         waitForPortToBeReleased();
        
//         // Start server before each test
//         server = new MathFunctionServer();
//         serverThread = new Thread(() -> server.startServer());
//         serverThread.setDaemon(true);
//         serverThread.start();
        
//         // Wait for server to start
//         waitForServerToStart();
//     }

//     @AfterEach
//     void tearDown() throws InterruptedException {
//         // Stop server after each test
//         if (server != null) {
//             server.stopServer();
//         }
//         if (serverThread != null) {
//             serverThread.interrupt();
//         }
//         // Give more time for proper cleanup - using wait loop instead of sleep
//         waitForPortToBeReleased();
//     }

//     /**
//      * Waits for the port to be released
//      */
//     private void waitForPortToBeReleased() throws InterruptedException {
//         for (int i = 0; i < 30; i++) {
//             try (Socket testSocket = new Socket()) {
//                 testSocket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), 100);
//                 // If connection succeeds, port is still occupied, wait a bit
//                 synchronized (this) {
//                     this.wait(200);
//                 }
//             } catch (IOException e) {
//                 // Port is free
//                 return;
//             }
//         }
//     }

//     /**
//      * Waits for the server to start by attempting connections
//      */
//     private void waitForServerToStart() throws InterruptedException {
//         for (int i = 0; i < 50; i++) {
//             try (Socket testSocket = new Socket(SERVER_HOST, SERVER_PORT)) {
//                 // If connection succeeds, server is ready
//                 return;
//             } catch (IOException e) {
//                 // Server not ready yet, wait a bit
//                 synchronized (this) {
//                     this.wait(100);
//                 }
//             }
//         }
//         // If we get here, server failed to start
//         throw new RuntimeException("Server failed to start after waiting 5 seconds");
//     }

//     @Test
//     void testDefaultCosineFunction() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Test cos(0) = 1
//             out.println("0");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("1.000000"), "cos(0) should be 1.000000, got: " + response);

//             // Test cos(π/2) ≈ 0
//             out.println("1.5707963267948966"); // π/2
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("0.000000"), "cos(π/2) should be approximately 0.000000, got: " + response);
//         }
//     }

//     @Test
//     void testFunctionSwitchingToSine() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Switch to sine function
//             out.println("fun:sin");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Función cambiada a: seno"), "Should confirm function change to sine, got: " + response);

//             // Test sin(0) = 0
//             out.println("0");
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("0.000000"), "sin(0) should be 0.000000, got: " + response);

//             // Test sin(π/2) = 1
//             out.println("1.5707963267948966"); // π/2
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("1.000000"), "sin(π/2) should be 1.000000, got: " + response);
//         }
//     }

//     @Test
//     void testFunctionSwitchingToTangent() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Switch to tangent function
//             out.println("fun:tan");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Función cambiada a: tangente"), "Should confirm function change to tangent, got: " + response);

//             // Test tan(0) = 0
//             out.println("0");
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("0.000000"), "tan(0) should be 0.000000, got: " + response);

//             // Test tan(π/4) = 1
//             out.println("0.7853981633974483"); // π/4
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("1.000000"), "tan(π/4) should be 1.000000, got: " + response);
//         }
//     }

//     @Test
//     void testInvalidFunctionCommand() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Try invalid function
//             out.println("fun:log");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Error - Función 'log' no reconocida"), "Should show error for invalid function, got: " + response);
//         }
//     }

//     @Test
//     void testInvalidNumberInput() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Try invalid number
//             out.println("abc");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Error - 'abc' no es un número válido"), "Should show error for invalid number, got: " + response);
//         }
//     }

//     @Test
//     void testMultipleFunctionSwitches() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Start with cos, test cos(0) = 1
//             out.println("0");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("1.000000"), "Initial cos(0) should be 1.000000, got: " + response);

//             // Switch to sin
//             out.println("fun:sin");
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Función cambiada a: seno"), "Should confirm switch to sine, got: " + response);

//             // Test sin(0) = 0
//             out.println("0");
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("0.000000"), "sin(0) should be 0.000000, got: " + response);

//             // Switch back to cos
//             out.println("fun:cos");
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Función cambiada a: coseno"), "Should confirm switch back to cosine, got: " + response);

//             // Test cos(0) = 1 again
//             out.println("0");
//             response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("1.000000"), "cos(0) should be 1.000000 again, got: " + response);
//         }
//     }

//     @Test
//     void testDecimalNumbers() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Test with decimal number
//             out.println("0.5");
//             String response = in.readLine();
//             assertNotNull(response);
//             // cos(0.5) ≈ 0.877583
//             assertTrue(response.contains("0.877583"), "cos(0.5) should be approximately 0.877583, got: " + response);
//         }
//     }

//     @Test
//     void testNegativeNumbers() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Test with negative number
//             out.println("-1");
//             String response = in.readLine();
//             assertNotNull(response);
//             // cos(-1) ≈ 0.540302
//             assertTrue(response.contains("0.540302"), "cos(-1) should be approximately 0.540302, got: " + response);
//         }
//     }

//     @Test
//     void testEmptyInput() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Test empty input
//             out.println("");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Error - Entrada vacía"), "Should show error for empty input, got: " + response);
//         }
//     }

//     @Test
//     void testSpecialValues() throws IOException {
//         try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             // Switch to tangent for testing special values
//             out.println("fun:tan");
//             String response = in.readLine();
//             assertNotNull(response);
//             assertTrue(response.contains("Función cambiada a: tangente"), "Should confirm switch to tangent, got: " + response);

//             // Test tan(π/2) which should be very large (approaching infinity)
//             out.println("1.5707963267948966"); // π/2
//             response = in.readLine();
//             assertNotNull(response);
//             // The result should be a very large number
//             assertTrue(response.contains("Respuesta:"), "Should return a valid response, got: " + response);
//         }
//     }
// }
