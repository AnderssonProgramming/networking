package edu.eci.arsw.networking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Test class for URLAnalyzer
 * Verifies that the URL methods return the expected values
 */
class URLAnalyzerTest {

    @Test
    @SuppressWarnings("deprecation")
    void testURLMethods() throws MalformedURLException {
        // Create a comprehensive URL for testing
        // Using @SuppressWarnings for educational purposes in this networking exercise
        URL url = new URL("https://www.example.com:8080/path/to/resource?param1=value1&param2=value2#section1");
        
        // Test all 8 URL methods
        assertEquals("https", url.getProtocol());
        assertEquals("www.example.com:8080", url.getAuthority());
        assertEquals("www.example.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("/path/to/resource", url.getPath());
        assertEquals("param1=value1&param2=value2", url.getQuery());
        assertEquals("/path/to/resource?param1=value1&param2=value2", url.getFile());
        assertEquals("section1", url.getRef());
    }
    
    @Test
    @SuppressWarnings("deprecation")
    void testURLWithDefaultPort() throws MalformedURLException {
        // Test URL with default port (should return -1)
        // Using @SuppressWarnings for educational purposes in this networking exercise
        URL url = new URL("http://www.example.com/test");
        
        assertEquals("http", url.getProtocol());
        assertEquals("www.example.com", url.getAuthority());
        assertEquals("www.example.com", url.getHost());
        assertEquals(-1, url.getPort()); // Default port returns -1
        assertEquals("/test", url.getPath());
        assertNull(url.getQuery());
        assertEquals("/test", url.getFile());
        assertNull(url.getRef());
    }
}
