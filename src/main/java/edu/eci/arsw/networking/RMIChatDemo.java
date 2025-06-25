package edu.eci.arsw.networking;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Exercise 6.4.1: RMI Chat Application Demo
 * Demonstration of the RMI-based chat system functionality.
 * 
 * This demo shows how to:
 * 1. Start multiple RMI chat instances
 * 2. Connect them to each other
 * 3. Exchange messages bidirectionally
 * 4. Handle connection scenarios
 * 
 * The demo can run in both automated and interactive modes.
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class RMIChatDemo {
    
    private static final int DEMO_PORT_1 = 1091;
    private static final int DEMO_PORT_2 = 1092;
    private static final String SERVICE_NAME = "ChatService";
    
    /**
     * Runs an automated demo showing RMI chat functionality
     */
    public static void runAutomatedDemo() {
        System.out.println("=== RMI Chat Application - Automated Demo ===");
        System.out.println("Exercise 6.4.1 - Java RMI Bidirectional Chat");
        System.out.println();
        
        try {
            // Create two chat instances
            System.out.println("Step 1: Creating two RMI chat instances...");
            RMIChatApplication alice = new RMIChatApplication("Alice");
            RMIChatApplication bob = new RMIChatApplication("Bob");
            
            // Set up RMI registries
            System.out.println("Step 2: Setting up RMI registries...");
            Registry registry1 = LocateRegistry.createRegistry(DEMO_PORT_1);
            Registry registry2 = LocateRegistry.createRegistry(DEMO_PORT_2);
            
            // Bind services to registries
            registry1.rebind(SERVICE_NAME, alice);
            registry2.rebind(SERVICE_NAME, bob);
            
            System.out.println("  - Alice's chat service published on port " + DEMO_PORT_1);
            System.out.println("  - Bob's chat service published on port " + DEMO_PORT_2);
            
            // Get remote references
            System.out.println("Step 3: Establishing remote connections...");
            ChatService aliceRemote = (ChatService) LocateRegistry.getRegistry("localhost", DEMO_PORT_1).lookup(SERVICE_NAME);
            ChatService bobRemote = (ChatService) LocateRegistry.getRegistry("localhost", DEMO_PORT_2).lookup(SERVICE_NAME);
            
            System.out.println("  - Alice connected to Bob's service");
            System.out.println("  - Bob connected to Alice's service");
            
            // Test basic functionality
            System.out.println("Step 4: Testing basic functionality...");
            System.out.println("  - Alice's name: " + aliceRemote.getParticipantName());
            System.out.println("  - Bob's name: " + bobRemote.getParticipantName());
            System.out.println("  - Alice ping: " + aliceRemote.ping());
            System.out.println("  - Bob ping: " + bobRemote.ping());
            
            // Demonstrate message exchange
            System.out.println("Step 5: Demonstrating message exchange...");
            System.out.println();
            
            // Create message capture mechanism
            final CountDownLatch messagesReceived = new CountDownLatch(4);
            
            // Override receiveMessage to show demo output
            RMIChatApplication demoAlice = new RMIChatApplication("Alice") {
                @Override
                public void receiveMessage(String senderName, String message) throws RemoteException {
                    System.out.println("  [Alice received] " + senderName + ": " + message);
                    messagesReceived.countDown();
                }
            };
            
            RMIChatApplication demoBob = new RMIChatApplication("Bob") {
                @Override
                public void receiveMessage(String senderName, String message) throws RemoteException {
                    System.out.println("  [Bob received] " + senderName + ": " + message);
                    messagesReceived.countDown();
                }
            };
            
            // Re-bind with demo instances
            registry1.rebind(SERVICE_NAME, demoAlice);
            registry2.rebind(SERVICE_NAME, demoBob);
            
            // Get new remote references
            ChatService demoAliceRemote = (ChatService) LocateRegistry.getRegistry("localhost", DEMO_PORT_1).lookup(SERVICE_NAME);
            ChatService demoBobRemote = (ChatService) LocateRegistry.getRegistry("localhost", DEMO_PORT_2).lookup(SERVICE_NAME);
            
            // Send messages between participants
            System.out.println("  Sending messages...");
            demoAliceRemote.receiveMessage("Bob", "Hello Alice! How are you?");
            Thread.sleep(500);
            
            demoBobRemote.receiveMessage("Alice", "Hi Bob! I'm doing great. How about you?");
            Thread.sleep(500);
            
            demoAliceRemote.receiveMessage("Bob", "I'm good too! This RMI chat is working well!");
            Thread.sleep(500);
            
            demoBobRemote.receiveMessage("Alice", "Yes, it's really cool how RMI enables distributed communication!");
            
            // Wait for all messages to be processed
            if (messagesReceived.await(5, TimeUnit.SECONDS)) {
                System.out.println();
                System.out.println("Step 6: All messages exchanged successfully!");
            } else {
                System.out.println("Warning: Some messages may not have been received in time.");
            }
            
            // Clean up
            System.out.println("Step 7: Cleaning up resources...");
            registry1.unbind(SERVICE_NAME);
            registry2.unbind(SERVICE_NAME);
            
            System.out.println();            System.out.println("=== Demo completed successfully! ===");
            System.out.println();
            printUsageInstructions();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Demo interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs an interactive demo where users can manually test the chat
     */
    public static void runInteractiveDemo() {
        System.out.println("=== RMI Chat Application - Interactive Demo ===");
        System.out.println("Exercise 6.4.1 - Java RMI Bidirectional Chat");
        System.out.println();
        
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("This demo will guide you through setting up an RMI chat session.");
            System.out.println("You'll need to open multiple terminals to see the full functionality.");
            System.out.println();
            
            System.out.print("Choose demo mode (1=Automated, 2=Manual setup guide): ");
            String choice = scanner.nextLine().trim();
            
            if ("2".equals(choice)) {
                printManualSetupGuide();
            } else {
                runAutomatedDemo();
            }
            
        } catch (Exception e) {
            System.err.println("Interactive demo failed: " + e.getMessage());
        }
    }
    
    /**
     * Prints manual setup instructions for users
     */
    private static void printManualSetupGuide() {
        System.out.println("=== Manual Setup Guide ===");
        System.out.println();
        System.out.println("To manually test the RMI Chat Application:");
        System.out.println();
        System.out.println("1. Compile the project:");
        System.out.println("   mvn compile");
        System.out.println();
        System.out.println("2. Open Terminal 1 (First chat participant):");
        System.out.println("   java -cp target/classes edu.eci.arsw.networking.RMIChatApplication");
        System.out.println("   - Enter name: Alice");
        System.out.println("   - Enter local port: 1099");
        System.out.println("   - Connect to remote peer: n");
        System.out.println();
        System.out.println("3. Open Terminal 2 (Second chat participant):");
        System.out.println("   java -cp target/classes edu.eci.arsw.networking.RMIChatApplication");
        System.out.println("   - Enter name: Bob");
        System.out.println("   - Enter local port: 1100");
        System.out.println("   - Connect to remote peer: y");
        System.out.println("   - Remote host: localhost (or press Enter)");
        System.out.println("   - Remote port: 1099");
        System.out.println();
        System.out.println("4. Start chatting:");
        System.out.println("   - Type messages in either terminal");
        System.out.println("   - Messages will appear in both terminals");
        System.out.println("   - Type 'exit' to quit");
        System.out.println("   - Type 'status' to check connection");
        System.out.println();
        System.out.println("5. Test scenarios:");
        System.out.println("   - Send messages from both sides");
        System.out.println("   - Try closing one terminal and see error handling");
        System.out.println("   - Restart and reconnect");
        System.out.println();
    }
    
    /**
     * Prints usage instructions for the RMI Chat Application
     */
    private static void printUsageInstructions() {
        System.out.println("=== Usage Instructions ===");
        System.out.println();
        System.out.println("Command Line Usage:");
        System.out.println("  java -cp target/classes edu.eci.arsw.networking.RMIChatApplication");
        System.out.println();
        System.out.println("Features:");
        System.out.println("  • Bidirectional RMI-based chat communication");
        System.out.println("  • Each instance acts as both client and server");
        System.out.println("  • Configurable local and remote ports");
        System.out.println("  • Real-time message timestamps");
        System.out.println("  • Connection status monitoring");
        System.out.println("  • Graceful error handling and recovery");
        System.out.println();
        System.out.println("Chat Commands:");
        System.out.println("  • Type any message and press Enter to send");
        System.out.println("  • 'exit' - Quit the application");
        System.out.println("  • 'status' - Check connection status");
        System.out.println();
        System.out.println("Network Ports:");
        System.out.println("  • Use ports 1024-65535 for RMI services");
        System.out.println("  • Each chat instance needs its own unique port");
        System.out.println("  • Common ports: 1099, 1100, 1101, etc.");
        System.out.println();
    }
    
    /**
     * Main method - entry point for the demo
     * 
     * @param args Command line arguments:
     *             "auto" for automated demo
     *             "interactive" for interactive demo
     *             "manual" for manual setup guide
     */
    public static void main(String[] args) {
        if (args.length > 0) {            switch (args[0].toLowerCase()) {
                case "auto", "automated":
                    runAutomatedDemo();
                    break;
                case "interactive":
                    runInteractiveDemo();
                    break;
                case "manual":
                    printManualSetupGuide();
                    break;
                default:
                    System.out.println("Usage: java RMIChatDemo [auto|interactive|manual]");
                    runInteractiveDemo();
                    break;
            }
        } else {
            runInteractiveDemo();
        }
    }
}
