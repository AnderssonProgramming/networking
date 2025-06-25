package edu.eci.arsw.networking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Timeout;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RMI Chat Application (Exercise 6.4.1)
 * 
 * This test verifies the functionality of the RMI-based chat system,
 * including remote service registration, message sending, and communication
 * between multiple chat instances.
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class RMIChatApplicationTest {
      private static final int TEST_PORT_1 = 1097;
    private static final int TEST_PORT_2 = 1098;
    private static final int TEST_PORT_3 = 1096;
    private static final int TEST_PORT_4 = 1095;
    private static final String SERVICE_NAME = "ChatService";
    private static final String TEST_PARTICIPANT_1 = "TestUser1";
    private static final String TEST_PARTICIPANT_2 = "TestUser2";
      private RMIChatApplication chatApp1;
    private RMIChatApplication chatApp2;
    private Registry registry1;
    private Registry registry2;
    
    @BeforeEach
    void setUp() throws RemoteException {
        // Create two chat application instances for testing
        chatApp1 = new RMIChatApplication(TEST_PARTICIPANT_1);
        chatApp2 = new RMIChatApplication(TEST_PARTICIPANT_2);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up RMI registries
        try {
            if (registry1 != null) {
                registry1.unbind(SERVICE_NAME);
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
        
        try {
            if (registry2 != null) {
                registry2.unbind(SERVICE_NAME);
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    @Timeout(10)
    void testChatServiceBasicFunctionality() throws RemoteException {
        // Test basic remote interface methods
        assertEquals(TEST_PARTICIPANT_1, chatApp1.getParticipantName());
        assertEquals(TEST_PARTICIPANT_2, chatApp2.getParticipantName());
        assertTrue(chatApp1.ping());
        assertTrue(chatApp2.ping());
    }
    
    @Test
    @Timeout(15)
    void testRMIServiceRegistration() throws Exception {
        // Create and bind to RMI registry
        registry1 = LocateRegistry.createRegistry(TEST_PORT_1);
        registry1.rebind(SERVICE_NAME, chatApp1);
        
        // Verify service is registered
        ChatService retrievedService = (ChatService) registry1.lookup(SERVICE_NAME);
        assertNotNull(retrievedService);
        assertEquals(TEST_PARTICIPANT_1, retrievedService.getParticipantName());
        assertTrue(retrievedService.ping());
    }
    
    @Test
    @Timeout(15)
    void testRemoteServiceConnection() throws Exception {
        // Set up first chat service
        registry1 = LocateRegistry.createRegistry(TEST_PORT_1);
        registry1.rebind(SERVICE_NAME, chatApp1);
        
        // Connect from another registry
        Registry clientRegistry = LocateRegistry.getRegistry("localhost", TEST_PORT_1);
        ChatService remoteService = (ChatService) clientRegistry.lookup(SERVICE_NAME);
        
        // Verify connection works
        assertNotNull(remoteService);
        assertEquals(TEST_PARTICIPANT_1, remoteService.getParticipantName());
        assertTrue(remoteService.ping());
    }
    
    @Test
    @Timeout(20)
    void testBidirectionalCommunication() throws Exception {
        // Set up both chat services
        registry1 = LocateRegistry.createRegistry(TEST_PORT_1);
        registry2 = LocateRegistry.createRegistry(TEST_PORT_2);
        
        registry1.rebind(SERVICE_NAME, chatApp1);
        registry2.rebind(SERVICE_NAME, chatApp2);
        
        // Create test message recipients to capture received messages
        final AtomicReference<String> receivedMessage1 = new AtomicReference<>();
        final AtomicReference<String> receivedSender1 = new AtomicReference<>();
        final AtomicReference<String> receivedMessage2 = new AtomicReference<>();
        final AtomicReference<String> receivedSender2 = new AtomicReference<>();
        
        final CountDownLatch messageReceived1 = new CountDownLatch(1);
        final CountDownLatch messageReceived2 = new CountDownLatch(1);
        
        // Create custom chat implementations that capture messages
        RMIChatApplication testChatApp1 = new RMIChatApplication(TEST_PARTICIPANT_1) {
            @Override
            public void receiveMessage(String senderName, String message) throws RemoteException {
                receivedSender1.set(senderName);
                receivedMessage1.set(message);
                messageReceived1.countDown();
            }
        };
        
        RMIChatApplication testChatApp2 = new RMIChatApplication(TEST_PARTICIPANT_2) {
            @Override
            public void receiveMessage(String senderName, String message) throws RemoteException {
                receivedSender2.set(senderName);
                receivedMessage2.set(message);
                messageReceived2.countDown();
            }
        };
        
        // Register the test chat applications
        registry1.rebind(SERVICE_NAME, testChatApp1);
        registry2.rebind(SERVICE_NAME, testChatApp2);
        
        // Get remote references
        Registry clientRegistry1 = LocateRegistry.getRegistry("localhost", TEST_PORT_1);
        Registry clientRegistry2 = LocateRegistry.getRegistry("localhost", TEST_PORT_2);
        
        ChatService remoteChat1 = (ChatService) clientRegistry1.lookup(SERVICE_NAME);
        ChatService remoteChat2 = (ChatService) clientRegistry2.lookup(SERVICE_NAME);
        
        // Send messages between services
        String testMessage1 = "Hello from " + TEST_PARTICIPANT_2;
        String testMessage2 = "Hello from " + TEST_PARTICIPANT_1;
        
        remoteChat1.receiveMessage(TEST_PARTICIPANT_2, testMessage1);
        remoteChat2.receiveMessage(TEST_PARTICIPANT_1, testMessage2);
        
        // Wait for messages to be received
        assertTrue(messageReceived1.await(5, TimeUnit.SECONDS), 
                   "Message 1 should be received within timeout");
        assertTrue(messageReceived2.await(5, TimeUnit.SECONDS), 
                   "Message 2 should be received within timeout");
        
        // Verify messages were received correctly
        assertEquals(TEST_PARTICIPANT_2, receivedSender1.get());
        assertEquals(testMessage1, receivedMessage1.get());
        assertEquals(TEST_PARTICIPANT_1, receivedSender2.get());
        assertEquals(testMessage2, receivedMessage2.get());
    }
    
    @Test
    @Timeout(10)
    void testMultipleConsecutiveMessages() throws Exception {
        // Set up chat service
        registry1 = LocateRegistry.createRegistry(TEST_PORT_1);
        
        final StringBuilder receivedMessages = new StringBuilder();
        final CountDownLatch messagesReceived = new CountDownLatch(3);
        
        RMIChatApplication testChatApp = new RMIChatApplication(TEST_PARTICIPANT_1) {
            @Override
            public void receiveMessage(String senderName, String message) throws RemoteException {
                receivedMessages.append(senderName).append(": ").append(message).append(";");
                messagesReceived.countDown();
            }
        };
        
        registry1.rebind(SERVICE_NAME, testChatApp);
        
        // Get remote reference and send multiple messages
        Registry clientRegistry = LocateRegistry.getRegistry("localhost", TEST_PORT_1);
        ChatService remoteChat = (ChatService) clientRegistry.lookup(SERVICE_NAME);
        
        remoteChat.receiveMessage("User1", "Message 1");
        remoteChat.receiveMessage("User2", "Message 2");
        remoteChat.receiveMessage("User1", "Message 3");
        
        // Wait for all messages
        assertTrue(messagesReceived.await(5, TimeUnit.SECONDS), 
                   "All messages should be received within timeout");
        
        String allMessages = receivedMessages.toString();
        assertTrue(allMessages.contains("User1: Message 1"));
        assertTrue(allMessages.contains("User2: Message 2"));
        assertTrue(allMessages.contains("User1: Message 3"));
    }
    
    @Test
    @Timeout(10)
    void testConnectionFailureHandling() {
        // Test connecting to non-existent service
        assertThrows(Exception.class, () -> {
            Registry nonExistentRegistry = LocateRegistry.getRegistry("localhost", 9999);
            nonExistentRegistry.lookup(SERVICE_NAME);
        }, "Should throw exception when connecting to non-existent service");
    }
    
    @Test
    @Timeout(10)
    void testEmptyAndSpecialCharacterMessages() throws Exception {
        registry1 = LocateRegistry.createRegistry(TEST_PORT_1);
        
        final AtomicReference<String> lastMessage = new AtomicReference<>();
        final CountDownLatch messageReceived = new CountDownLatch(1);
        
        RMIChatApplication testChatApp = new RMIChatApplication(TEST_PARTICIPANT_1) {
            @Override
            public void receiveMessage(String senderName, String message) throws RemoteException {
                lastMessage.set(message);
                messageReceived.countDown();
            }
        };
        
        registry1.rebind(SERVICE_NAME, testChatApp);
        
        Registry clientRegistry = LocateRegistry.getRegistry("localhost", TEST_PORT_1);
        ChatService remoteChat = (ChatService) clientRegistry.lookup(SERVICE_NAME);
        
        // Test special characters and emojis
        String specialMessage = "Hello! @#$%^&*()_+ 你好 🎉🚀";
        remoteChat.receiveMessage("TestUser", specialMessage);
        
        assertTrue(messageReceived.await(5, TimeUnit.SECONDS));
        assertEquals(specialMessage, lastMessage.get());
    }
}
