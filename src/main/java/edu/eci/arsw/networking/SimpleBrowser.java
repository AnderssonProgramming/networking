package edu.eci.arsw.networking;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

/**
 * Exercise 2: Simple Browser Application
 * This program asks the user for a URL address, reads data from that URL,
 * and stores it in a file named "resultado.html".
 * 
 * Based on the networking tutorial by Luis Daniel Benavides Navarro
 * Escuela Colombiana de Ingeniería - Arquitectura Empresarial
 * 
 * @author Andersson David Sánchez Méndez
 * @version 1.0
 */
public class SimpleBrowser {
    
    private static final String OUTPUT_FILE = "resultado.html";
      public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Ask user for URL
            System.out.println("=== SIMPLE BROWSER APPLICATION ===");
            System.out.print("Ingrese la dirección URL: ");
            String urlString = scanner.nextLine();
            
            // Validate and process URL
            if (urlString.trim().isEmpty()) {
                System.err.println("Error: URL no puede estar vacía");
                return;
            }
            
            // Add protocol if missing
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://" + urlString;
                System.out.println("Agregando protocolo HTTP: " + urlString);
            }
            
            // Download and save the web page
            downloadWebPage(urlString);
            
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Downloads a web page from the given URL and saves it to resultado.html
     * @param urlString The URL to download from
     */
    private static void downloadWebPage(String urlString) {
        try {
            System.out.println("Conectando a: " + urlString);
            
            // Create URL object
            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            
            // Open connection
            URLConnection connection = url.openConnection();
            
            // Set user agent to avoid being blocked by some servers
            connection.setRequestProperty("User-Agent", "SimpleBrowser/1.0");
            
            // Set connection timeout
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000); // 10 seconds
            
            // Get connection info
            String contentType = connection.getContentType();
            int contentLength = connection.getContentLength();
            
            System.out.println("Tipo de contenido: " + (contentType != null ? contentType : "Desconocido"));
            System.out.println("Tamaño del contenido: " + (contentLength != -1 ? contentLength + " bytes" : "Desconocido"));
            
            // Create input stream and readers/writers with try-with-resources
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                 FileWriter fileWriter = new FileWriter(OUTPUT_FILE);
                 PrintWriter writer = new PrintWriter(fileWriter, true)) {
                
                // Read and write data
                String line;
                int linesRead = 0;
                long bytesRead = 0;
                
                System.out.println("Descargando contenido...");
                
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                    linesRead++;
                    bytesRead += line.getBytes(StandardCharsets.UTF_8).length + 1; // +1 for newline
                    
                    // Show progress every 100 lines
                    if (linesRead % 100 == 0) {
                        System.out.print(".");
                    }
                }
                
                System.out.println("\n¡Descarga completada!");
                System.out.println("Líneas leídas: " + linesRead);
                System.out.println("Bytes aproximados: " + bytesRead);
                System.out.println("Archivo guardado como: " + new File(OUTPUT_FILE).getAbsolutePath());
                System.out.println("\nPuede abrir el archivo '" + OUTPUT_FILE + "' en su navegador web.");
            }
            
        } catch (MalformedURLException e) {
            System.err.println("Error: URL malformada - " + e.getMessage());
            System.err.println("Asegúrese de que la URL tenga el formato correcto (ej: http://www.example.com)");
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
            if (e.getMessage().contains("Connection refused")) {
                System.err.println("El servidor rechazó la conexión. Verifique que la URL sea accesible.");
            } else if (e.getMessage().contains("UnknownHostException")) {
                System.err.println("No se pudo resolver el nombre del host. Verifique la URL y su conexión a internet.");
            }
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Alternative method to download using try-with-resources (more modern approach)
     * @param urlString The URL to download from
     */
    @SuppressWarnings("unused")
    private static void downloadWebPageModern(String urlString) {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "SimpleBrowser/1.0");
            
            // Try-with-resources automatically closes streams
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE), true)) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
                
                System.out.println("Archivo guardado como: " + OUTPUT_FILE);
                
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
