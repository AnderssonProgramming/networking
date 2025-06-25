package edu.eci.arsw.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Exercise 6.4.1: RMI Chat Service Interface
 * Remote interface for bidirectional chat communication using Java RMI.
 * 
 * This interface defines the methods that can be called remotely between
 * chat participants, enabling real-time message exchange.
 * 
 * Features:
 * - Send chat messages to remote peers
 * - Receive notification of incoming messages
 * - Support for user identification
 * 
 * Based on the networking tutorial by Andersson David Sánchez Méndez
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author GitHub Copilot Implementation
 * @version 1.0
 */
public interface ChatService extends Remote {
    
    /**
     * Sends a chat message to this remote service
     * 
     * @param senderName The name of the message sender
     * @param message The message content to send
     * @throws RemoteException If there is a communication error
     */
    void receiveMessage(String senderName, String message) throws RemoteException;
    
    /**
     * Gets the name/identifier of this chat service instance
     * 
     * @return The name of this chat participant
     * @throws RemoteException If there is a communication error
     */
    String getParticipantName() throws RemoteException;
    
    /**
     * Pings the remote service to check if it's alive
     * 
     * @return true if the service is responding
     * @throws RemoteException If there is a communication error
     */
    boolean ping() throws RemoteException;
}
