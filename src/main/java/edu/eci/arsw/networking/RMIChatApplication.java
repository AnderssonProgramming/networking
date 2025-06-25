package edu.eci.arsw.networking;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Exercise 6.4.1: RMI Chat Application
 * A chat application using Java RMI that allows bidirectional communication
 * between instances. Each instance acts as both client and server.
 * 
 * Features:
 * - RMI-based communication between chat participants
 * - Each instance can send and receive messages
 * - User-friendly console interface
 * - Connection to remote peers via IP and port
 * - Publishing own remote object for others to connect
 * 
 * Usage:
 * 1. Run multiple instances of this application
 * 2. Each instance will prompt for:
 *    - Local port to publish RMI service
 *    - Remote IP and port to connect to another participant
 * 3. Type messages to send to the connected peer
 * 4. Type 'exit' to quit the application
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public class RMIChatApplication extends UnicastRemoteObject implements ChatService {
    
    private static final String SERVICE_NAME = "ChatService";
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    private String participantName;
    private ChatService remoteChatService;
    private Scanner scanner;
    private boolean isConnected = false;
    
    /**
     * Constructor for RMI Chat Application
     * 
     * @param participantName The name of this chat participant
     * @throws RemoteException If RMI initialization fails
     */
    public RMIChatApplication(String participantName) throws RemoteException {
        super();
        this.participantName = participantName;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Implementation of ChatService.receiveMessage()
     * Called remotely when another participant sends a message
     */    @Override
    public void receiveMessage(String senderName, String message) throws RemoteException {
        String timestamp = timeFormat.format(new Date());
        System.out.println(String.format("[%s] %s: %s", timestamp, senderName, message));
        System.out.print("> "); // Re-prompt for user input
        System.out.flush();
    }
    
    /**
     * Implementation of ChatService.getParticipantName()
     */
    @Override
    public String getParticipantName() throws RemoteException {
        return this.participantName;
    }
    
    /**
     * Implementation of ChatService.ping()
     */
    @Override
    public boolean ping() throws RemoteException {
        return true;
    }
    
    /**
     * Starts the RMI registry and publishes this chat service
     * 
     * @param port The port to publish the RMI service on
     */
    private void startRMIService(int port) {
        try {
            // Create RMI registry on specified port
            Registry registry = LocateRegistry.createRegistry(port);
            
            // Bind this service to the registry
            registry.rebind(SERVICE_NAME, this);
            
            System.out.println(String.format("Chat service '%s' published on port %d", 
                               participantName, port));
            
        } catch (Exception e) {
            System.err.println("Failed to start RMI service: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Connects to a remote chat service
     * 
     * @param remoteHost The IP address of the remote host
     * @param remotePort The port of the remote RMI service
     */
    private void connectToRemotePeer(String remoteHost, int remotePort) {
        try {
            // Locate remote registry
            Registry registry = LocateRegistry.getRegistry(remoteHost, remotePort);
            
            // Look up the remote chat service
            this.remoteChatService = (ChatService) registry.lookup(SERVICE_NAME);
            
            // Test the connection
            String remoteName = remoteChatService.getParticipantName();
            System.out.println(String.format("Connected to remote peer: %s at %s:%d", 
                               remoteName, remoteHost, remotePort));
            
            this.isConnected = true;
            
        } catch (Exception e) {
            System.err.println("Failed to connect to remote peer: " + e.getMessage());
            this.isConnected = false;
        }
    }
    
    /**
     * Sends a message to the connected remote peer
     * 
     * @param message The message to send
     */
    private void sendMessage(String message) {
        if (!isConnected || remoteChatService == null) {
            System.out.println("Not connected to any remote peer.");
            return;
        }
        
        try {
            remoteChatService.receiveMessage(this.participantName, message);
              // Echo the sent message locally with timestamp
            String timestamp = timeFormat.format(new Date());
            System.out.println(String.format("[%s] %s (you): %s", 
                               timestamp, participantName, message));
            
        } catch (RemoteException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            this.isConnected = false;
            System.out.println("Connection lost. You can try to reconnect later.");
        }
    }
    
    /**
     * Main interaction loop for sending messages
     */
    private void startChatLoop() {
        System.out.println("\\n=== RMI Chat Application ===");
        System.out.println("Type your messages and press Enter to send.");
        System.out.println("Type 'exit' to quit the application.");
        System.out.println("Type 'status' to check connection status.");
        System.out.println("===========================\\n");
        
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            
            if (input.equalsIgnoreCase("status")) {
                if (isConnected) {
                    try {
                        String remoteName = remoteChatService.getParticipantName();
                        System.out.println("Connected to: " + remoteName);
                    } catch (RemoteException e) {
                        System.out.println("Connection lost.");
                        isConnected = false;
                    }
                } else {
                    System.out.println("Not connected to any remote peer.");
                }
                continue;
            }
            
            if (!input.isEmpty()) {
                sendMessage(input);
            }
        }
    }
    
    /**
     * Gets user input for port number with validation
     * 
     * @param prompt The prompt message to display
     * @return A valid port number
     */
    private int getPortInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int port = Integer.parseInt(scanner.nextLine().trim());
                if (port >= 1024 && port <= 65535) {
                    return port;
                } else {
                    System.out.println("Please enter a port number between 1024 and 65535.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid port number.");
            }
        }
    }
    
    /**
     * Gets user input for IP address
     * 
     * @param prompt The prompt message to display
     * @return IP address as string
     */
    private String getIPInput(String prompt) {
        System.out.print(prompt);
        String ip = scanner.nextLine().trim();
        if (ip.isEmpty()) {
            return "localhost";
        }
        return ip;
    }
    
    /**
     * Main method to start the RMI Chat Application
     * 
     * @param args Command line arguments (not used)
     */    public static void main(String[] args) {
        System.out.println("=== RMI Chat Application ===");
        System.out.println("Exercise 6.4.1 - Java RMI Bidirectional Chat");
        System.out.println();
        
        try (Scanner input = new Scanner(System.in)) {
            // Get participant name
            System.out.print("Enter your name: ");
            String participantName = input.nextLine().trim();
            if (participantName.isEmpty()) {
                participantName = "User_" + System.currentTimeMillis();
                System.out.println("Using default name: " + participantName);
            }
            
            try {
                // Create chat application instance
                RMIChatApplication chatApp = new RMIChatApplication(participantName);
                
                // Get local port for publishing RMI service
                int localPort = chatApp.getPortInput("Enter local port to publish RMI service (e.g., 1099): ");
                
                // Start RMI service
                chatApp.startRMIService(localPort);
                
                // Ask if user wants to connect to a remote peer
                System.out.print("Connect to a remote peer? (y/n): ");
                String connect = input.nextLine().trim().toLowerCase();
                
                if (connect.equals("y") || connect.equals("yes")) {
                    String remoteHost = chatApp.getIPInput("Enter remote host IP (or press Enter for localhost): ");
                    int remotePort = chatApp.getPortInput("Enter remote port: ");
                    
                    chatApp.connectToRemotePeer(remoteHost, remotePort);
                } else {
                    System.out.println("Waiting for incoming connections...");
                    System.out.println("Other instances can connect to: localhost:" + localPort);
                }
                
                // Start the chat interaction loop
                chatApp.startChatLoop();
                
            } catch (RemoteException e) {
                System.err.println("Failed to initialize RMI Chat Application: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
