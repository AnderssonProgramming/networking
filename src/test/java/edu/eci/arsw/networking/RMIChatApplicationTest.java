// package edu.eci.arsw.networking;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Timeout;

// import java.rmi.RemoteException;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;
// import java.rmi.NotBoundException;
// import java.util.concurrent.atomic.AtomicInteger;

// import static org.junit.jupiter.api.Assertions.*;

// /**
//  * Test class for RMI Chat Application (Exercise 6.4.1)
//  * 
//  * This test verifies the functionality of the RMI-based chat system,
//  * including remote service registration, message sending, and communication
//  * between multiple chat instances.
//  * 
//  * @author GitHub Copilot Implementation
//  * @version 1.0
//  */
// public class RMIChatApplicationTest {
    
//     private static final AtomicInteger PORT_COUNTER = new AtomicInteger(1090);
//     private static final AtomicInteger SERVICE_COUNTER = new AtomicInteger(1);
    
//     private String serviceName;
//     private int registryPort;
//     private RMIChatApplication chatApp1;
//     private RMIChatApplication chatApp2;
//     private Registry registry;

//     @BeforeEach
//     void setUp() throws RemoteException {
//         // Use unique service name and port for each test
//         serviceName = "ChatService_" + SERVICE_COUNTER.getAndIncrement();
//         registryPort = PORT_COUNTER.getAndIncrement();
        
//         // Create registry for this test
//         registry = LocateRegistry.createRegistry(registryPort);
        
//         // Create two chat application instances for testing
//         chatApp1 = new RMIChatApplication("TestUser1_" + System.nanoTime());
//         chatApp2 = new RMIChatApplication("TestUser2_" + System.nanoTime());
//     }

//     @AfterEach
//     void tearDown() {
//         try {
//             // Clean up registry bindings
//             if (registry != null) {
//                 try {
//                     String[] bound = registry.list();
//                     for (String name : bound) {
//                         try {
//                             registry.unbind(name);
//                         } catch (NotBoundException | RemoteException e) {
//                             // Ignore cleanup errors
//                         }
//                     }
//                 } catch (RemoteException e) {
//                     // Ignore cleanup errors
//                 }
//             }
            
//             // Cleanup chat applications
//             if (chatApp1 != null) {
//                 try {
//                     chatApp1.disconnect();
//                 } catch (Exception e) {
//                     // Ignore cleanup errors
//                 }
//             }
//             if (chatApp2 != null) {
//                 try {
//                     chatApp2.disconnect();
//                 } catch (Exception e) {
//                     // Ignore cleanup errors
//                 }
//             }
            
//             // Wait a bit for cleanup
//             Thread.sleep(100);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//     }

//     @Test
//     @Timeout(10)
//     void testChatServiceBasicFunctionality() throws RemoteException {
//         // Test basic remote interface methods
//         assertNotNull(chatApp1.getParticipantName());
//         assertNotNull(chatApp2.getParticipantName());
        
//         // Test ping functionality
//         assertTrue(chatApp1.ping());
//         assertTrue(chatApp2.ping());
        
//         System.out.println("✓ Basic functionality test passed");
//     }

//     @Test
//     @Timeout(15)
//     void testRMIServiceRegistration() throws RemoteException {
//         // Register first chat service with unique name
//         String testServiceName = serviceName + "_1";
//         registry.rebind(testServiceName, chatApp1);
        
//         // Verify it's registered by looking it up
//         ChatService lookedUpService = (ChatService) registry.lookup(testServiceName);
//         assertNotNull(lookedUpService);
//         assertEquals(chatApp1.getParticipantName(), lookedUpService.getParticipantName());
        
//         System.out.println("✓ Service registration test passed");
//     }

//     @Test
//     @Timeout(15)
//     void testRemoteServiceConnection() throws RemoteException {
//         // Register both services with unique names
//         String service1Name = serviceName + "_1";
//         String service2Name = serviceName + "_2";
        
//         registry.rebind(service1Name, chatApp1);
//         registry.rebind(service2Name, chatApp2);
        
//         // Look up the services
//         ChatService service1 = (ChatService) registry.lookup(service1Name);
//         ChatService service2 = (ChatService) registry.lookup(service2Name);
        
//         // Test remote method calls
//         assertTrue(service1.ping());
//         assertTrue(service2.ping());
        
//         assertNotNull(service1.getParticipantName());
//         assertNotNull(service2.getParticipantName());
        
//         System.out.println("✓ Remote service connection test passed");
//     }

//     @Test
//     @Timeout(20)
//     void testBidirectionalCommunication() throws RemoteException, InterruptedException {
//         // Register both services
//         String service1Name = serviceName + "_1";
//         String service2Name = serviceName + "_2";
        
//         registry.rebind(service1Name, chatApp1);
//         registry.rebind(service2Name, chatApp2);
        
//         // Look up the services
//         ChatService service1 = (ChatService) registry.lookup(service1Name);
//         ChatService service2 = (ChatService) registry.lookup(service2Name);
        
//         // Test message sending from service1 to service2
//         String testMessage1 = "Hello from " + service1.getParticipantName();
//         service2.receiveMessage(service1.getParticipantName(), testMessage1);
        
//         // Test message sending from service2 to service1
//         String testMessage2 = "Hello from " + service2.getParticipantName();
//         service1.receiveMessage(service2.getParticipantName(), testMessage2);
        
//         // The above calls should not throw exceptions
//         // In a real implementation, you might want to verify the messages were received
        
//         System.out.println("✓ Bidirectional communication test passed");
//     }

//     @Test
//     @Timeout(30)
//     void testMultipleConsecutiveMessages() throws RemoteException {
//         // Register services
//         String service1Name = serviceName + "_1";
//         String service2Name = serviceName + "_2";
        
//         registry.rebind(service1Name, chatApp1);
//         registry.rebind(service2Name, chatApp2);
        
//         // Look up the services
//         ChatService service1 = (ChatService) registry.lookup(service1Name);
//         ChatService service2 = (ChatService) registry.lookup(service2Name);
        
//         // Send multiple messages
//         for (int i = 1; i <= 5; i++) {
//             String message1 = "Message " + i + " from " + service1.getParticipantName();
//             String message2 = "Reply " + i + " from " + service2.getParticipantName();
            
//             // Send message from service1 to service2
//             service2.receiveMessage(service1.getParticipantName(), message1);
            
//             // Send reply from service2 to service1
//             service1.receiveMessage(service2.getParticipantName(), message2);
//         }
        
//         System.out.println("✓ Multiple consecutive messages test passed");
//     }

//     @Test
//     @Timeout(15)
//     void testServiceDiscovery() throws RemoteException {
//         // Register multiple services
//         String service1Name = serviceName + "_1";
//         String service2Name = serviceName + "_2";
        
//         registry.rebind(service1Name, chatApp1);
//         registry.rebind(service2Name, chatApp2);
        
//         // List all registered services
//         String[] boundNames = registry.list();
//         assertTrue(boundNames.length >= 2);
        
//         // Check that our services are in the list
//         boolean found1 = false, found2 = false;
//         for (String name : boundNames) {
//             if (name.equals(service1Name)) found1 = true;
//             if (name.equals(service2Name)) found2 = true;
//         }
        
//         assertTrue(found1, "Service 1 should be registered");
//         assertTrue(found2, "Service 2 should be registered");
        
//         System.out.println("✓ Service discovery test passed");
//     }

//     @Test
//     @Timeout(10)
//     void testErrorHandling() throws RemoteException {
//         // Test that services handle null/empty messages gracefully
//         // Register a service
//         String testServiceName = serviceName + "_error_test";
//         registry.rebind(testServiceName, chatApp1);
        
//         ChatService service = (ChatService) registry.lookup(testServiceName);
        
//         // These should not crash the service
//         try {
//             service.receiveMessage("TestSender", "");
//             service.receiveMessage("TestSender", null);
//             service.receiveMessage("", "Test message");
//             service.receiveMessage(null, "Test message");
//         } catch (Exception e) {
//             // It's okay if these throw exceptions, as long as the service doesn't crash
//             System.out.println("Expected exception for invalid input: " + e.getMessage());
//         }
        
//         // Service should still be responsive
//         assertTrue(service.ping());
        
//         System.out.println("✓ Error handling test passed");
//     }

//     @Test
//     @Timeout(15) 
//     void testConcurrentConnections() throws RemoteException, InterruptedException {
//         // Register service
//         String testServiceName = serviceName + "_concurrent";
//         registry.rebind(testServiceName, chatApp1);
        
//         ChatService service = (ChatService) registry.lookup(testServiceName);
        
//         // Create multiple threads that interact with the service
//         Thread[] threads = new Thread[3];
//         for (int i = 0; i < threads.length; i++) {
//             final int threadId = i;
//             threads[i] = new Thread(() -> {
//                 try {
//                     for (int j = 0; j < 3; j++) {
//                         service.receiveMessage("Thread" + threadId, "Message " + j);
//                         assertTrue(service.ping());
//                     }
//                 } catch (RemoteException e) {
//                     fail("Thread " + threadId + " failed: " + e.getMessage());
//                 }
//             });
//         }
        
//         // Start all threads
//         for (Thread thread : threads) {
//             thread.start();
//         }
        
//         // Wait for all threads to complete
//         for (Thread thread : threads) {
//             thread.join(5000); // 5 second timeout per thread
//         }
        
//         // Service should still be responsive
//         assertTrue(service.ping());
        
//         System.out.println("✓ Concurrent connections test passed");
//     }
// }
