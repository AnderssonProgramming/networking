package edu.eci.arsw.networking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

/**
 * Test class for TimeServer
 * Verifies that the UDP time server correctly responds to time requests
 * according to Exercise 5.2.1 requirements
 */
class TimeServerTest {
    
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 45000;
    private static final String REQUEST_MESSAGE = "GET_TIME";
    private static final int SOCKET_TIMEOUT = 5000;
    
    private TimeServer server;
    private Thread serverThread;
    
    @BeforeEach
    void setUp() throws InterruptedException {
        // Start server in a separate thread for testing
        server = new TimeServer();
        serverThread = new Thread(() -> server.startServer());
        serverThread.setDaemon(true); // Daemon thread will not prevent JVM from exiting
        serverThread.start();
        
        // Give server time to start
        Thread.sleep(2000);
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
    void testServerStartsAndResponds() throws IOException {
        // Test that server starts and is running
        assertTrue(server.isRunning(), "Server should be running after startup");
        assertEquals(45000, server.getServerPort(), "Server should be on port 45000");
    }
    
    @Test
    void testTimeRequest() throws IOException {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(SOCKET_TIMEOUT);
            
            // Prepare request
            byte[] requestData = REQUEST_MESSAGE.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            DatagramPacket requestPacket = new DatagramPacket(
                requestData, 
                requestData.length, 
                serverAddress, 
                SERVER_PORT
            );
            
            // Send request
            clientSocket.send(requestPacket);
            
            // Receive response
            byte[] responseBuffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            clientSocket.receive(responsePacket);
            
            // Verify response
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            assertNotNull(response, "Response should not be null");
            assertFalse(response.trim().isEmpty(), "Response should not be empty");
            
            // Verify time format (should contain date and time)
            assertTrue(response.contains("-"), "Response should contain date separators");
            assertTrue(response.contains(":"), "Response should contain time separators");
            
            System.out.println("Received time from server: " + response);
        }
    }
    
    @Test
    void testMultipleRequests() throws IOException, InterruptedException {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(SOCKET_TIMEOUT);
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            
            // Send multiple requests
            for (int i = 0; i < 3; i++) {
                // Prepare request
                byte[] requestData = REQUEST_MESSAGE.getBytes();
                DatagramPacket requestPacket = new DatagramPacket(
                    requestData, 
                    requestData.length, 
                    serverAddress, 
                    SERVER_PORT
                );
                
                // Send request
                clientSocket.send(requestPacket);
                
                // Receive response
                byte[] responseBuffer = new byte[256];
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                clientSocket.receive(responsePacket);
                
                // Verify response
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                assertNotNull(response, "Response " + (i+1) + " should not be null");
                assertFalse(response.trim().isEmpty(), "Response " + (i+1) + " should not be empty");
                
                System.out.println("Request " + (i+1) + " - Received time: " + response);
                
                // Small delay between requests
                Thread.sleep(1000);
            }
        }
    }
    
    @Test
    void testServerHandlesInvalidRequests() throws IOException {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(SOCKET_TIMEOUT);
            
            // Send invalid request
            byte[] requestData = "INVALID_REQUEST".getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            DatagramPacket requestPacket = new DatagramPacket(
                requestData, 
                requestData.length, 
                serverAddress, 
                SERVER_PORT
            );
            
            // Send request
            clientSocket.send(requestPacket);
            
            // Receive response (server should still respond with time)
            byte[] responseBuffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            clientSocket.receive(responsePacket);
            
            // Verify response (server responds with time regardless of request content)
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            assertNotNull(response, "Response should not be null even for invalid requests");
            assertFalse(response.trim().isEmpty(), "Response should not be empty even for invalid requests");
            
            System.out.println("Response to invalid request: " + response);
        }
    }
    
    @Test
    void testServerStopsGracefully() {
        // Test that server can be stopped
        assertTrue(server.isRunning(), "Server should be running initially");
        
        server.stopServer();
        
        // Give some time for the server to stop
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertFalse(server.isRunning(), "Server should be stopped after calling stopServer()");
    }
}
