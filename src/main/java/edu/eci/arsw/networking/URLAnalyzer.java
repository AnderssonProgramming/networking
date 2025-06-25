package edu.eci.arsw.networking;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Exercise 1: URL Analysis Program
 * This program creates a URL object and prints the values returned by the 8 URL methods:
 * getProtocol, getAuthority, getHost, getPort, getPath, getQuery, getFile, getRef
 * 
 * @author Andersson David Sánchez Méndez
 * @version 1.0
 */
public class URLAnalyzer {
      public static void main(String[] args) {
        try {
            // Create a URL object with a comprehensive example that includes all components
            @SuppressWarnings("deprecation")
            URL url = new URL("https://www.nba.com:8080/path/to/resource?param1=value1&param2=value2#section1");
            
            // Print header
            System.out.println("=== URL ANALYSIS ===");
            System.out.println("URL: " + url.toString());
            System.out.println();
            
            // Analyze and print each component using the 8 URL methods
            analyzeURL(url);
            
            // Additional example with different URL structure
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== SECOND EXAMPLE ===");
            
            @SuppressWarnings("deprecation")
            URL url2 = new URL("http://localhost:3000/api/users");
            System.out.println("URL: " + url2.toString());
            System.out.println();
            analyzeURL(url2);
            
        } catch (MalformedURLException e) {
            System.err.println("Error creating URL: " + e.getMessage());
        }
    }
    
    /**
     * Analyzes a URL object and prints the values of all 8 URL methods
     * @param url The URL object to analyze
     */
    private static void analyzeURL(URL url) {
        System.out.printf("%-15s: %s%n", "Protocol", url.getProtocol());
        System.out.printf("%-15s: %s%n", "Authority", url.getAuthority());
        System.out.printf("%-15s: %s%n", "Host", url.getHost());
        System.out.printf("%-15s: %d%n", "Port", url.getPort());
        System.out.printf("%-15s: %s%n", "Path", url.getPath());
        System.out.printf("%-15s: %s%n", "Query", url.getQuery());
        System.out.printf("%-15s: %s%n", "File", url.getFile());
        System.out.printf("%-15s: %s%n", "Ref", url.getRef());
    }
}
