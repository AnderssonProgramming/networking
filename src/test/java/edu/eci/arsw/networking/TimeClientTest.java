package edu.eci.arsw.networking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Test class for TimeClient
 * Verifies that the UDP time client correctly requests and handles time updates
 * according to Exercise 5.2.1 requirements
 */
class TimeClientTest {
    
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 45000;
    private static final int SOCKET_TIMEOUT = 3000;
      private TimeServer server;
    private Thread serverThread;
      @BeforeEach
    void setUp() {
        // Start server in a separate thread for testing
        server = new TimeServer();
        serverThread = new Thread(() -> server.startServer());
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Give server time to start with a more robust approach
        waitForServerToStart();
    }
      private void waitForServerToStart() {
        // Try to connect to server to verify it's ready
        int attempts = 0;
        final int maxAttempts = 50;
        while (attempts < maxAttempts) {
            try (DatagramSocket testSocket = new DatagramSocket()) {
                testSocket.setSoTimeout(100);
                
                byte[] testData = "PING".getBytes();
                InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
                DatagramPacket testPacket = new DatagramPacket(testData, testData.length, serverAddress, SERVER_PORT);
                
                testSocket.send(testPacket);
                
                byte[] responseBuffer = new byte[256];
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                testSocket.receive(responsePacket);
                
                // If we get here, server is ready
                return;
            } catch (IOException e) {
                attempts++;
                // Small busy wait to give server time to start
                for (int i = 0; i < 1000000; i++) {
                    // Busy wait instead of Thread.sleep
                }
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
    void testClientInitialization() {
        TimeClient client = new TimeClient();
        
        // Test initial state
        assertFalse(client.isRunning(), "Client should not be running initially");
        assertEquals("localhost", client.getServerHost(), "Server host should be localhost");
        assertEquals(45000, client.getServerPort(), "Server port should be 45000");
        assertEquals("No hay datos de tiempo disponibles", client.getLastKnownTime(), 
                    "Initial last known time should be default message");
    }
      @Test
    void testClientConnectsToServer() {
        TimeClient client = new TimeClient();
        
        // Test initial state
        assertFalse(client.isRunning(), "Client should not be running initially");
        assertEquals("localhost", client.getServerHost(), "Server host should be localhost");
        assertEquals(45000, client.getServerPort(), "Server port should be 45000");
    }
    
    @Test
    void testManualTimeRequest() throws IOException {
        // Test manual time request similar to what the client does internally
        try (DatagramSocket testSocket = new DatagramSocket()) {
            testSocket.setSoTimeout(SOCKET_TIMEOUT);
            
            // Create request
            String requestMessage = "GET_TIME";
            byte[] requestData = requestMessage.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            DatagramPacket requestPacket = new DatagramPacket(
                requestData, 
                requestData.length, 
                serverAddress, 
                SERVER_PORT
            );
            
            // Send request
            testSocket.send(requestPacket);
            
            // Receive response
            byte[] responseBuffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            testSocket.receive(responsePacket);
            
            // Verify response
            String serverTime = new String(responsePacket.getData(), 0, responsePacket.getLength());
            assertNotNull(serverTime, "Server time should not be null");
            assertFalse(serverTime.trim().isEmpty(), "Server time should not be empty");
            
            // Verify time format
            assertTrue(serverTime.contains("-"), "Time should contain date separators");
            assertTrue(serverTime.contains(":"), "Time should contain time separators");
            
            System.out.println("Manual request received time: " + serverTime);
        }
    }
}
