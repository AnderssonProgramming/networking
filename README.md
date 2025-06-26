# Java Networking Programming Exercises

A comprehensive collection of Java networking programming exercises demonstrating various networking concepts, protocols, and communication patterns. This project implements client-server applications using different networking technologies including URL analysis, HTTP browsers, TCP sockets, UDP datagrams, and RMI (Remote Method Invocation).

## 📋 Table of Contents

- [Overview](#overview)
- [Exercises Implemented](#exercises-implemented)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running the Exercises](#running-the-exercises)
- [Testing](#testing)
- [Architecture Highlights](#architecture-highlights)
- [Requirements](#requirements)

## 🎯 Overview

This project demonstrates fundamental networking concepts in Java through practical implementations of:

- **URL manipulation and analysis**
- **HTTP client applications**  
- **TCP socket programming** (client-server communication)
- **UDP datagram communication** (connectionless protocols)
- **Java RMI** (distributed object communication)

Each exercise builds upon networking fundamentals while showcasing different communication patterns and protocols used in distributed systems.

## 🚀 Exercises Implemented

### 📌 Exercise 1: URL Analysis Program
**File:** `URLAnalyzer.java`

Demonstrates the 8 main methods of the `java.net.URL` class:
- Protocol, Authority, Host, Port extraction
- Path, Query, File, and Reference components
- Comprehensive URL component analysis

```bash
java -cp target/classes edu.eci.arsw.networking.URLAnalyzer
```

### 📌 Exercise 2: Simple Browser Application  
**File:** `SimpleBrowser.java`

HTTP client that downloads web pages and saves them locally:
- Interactive URL input with automatic protocol detection
- Content download with progress indicators
- Saves output to `resultado.html`
- Robust error handling for network operations

```bash
java -cp target/classes edu.eci.arsw.networking.SimpleBrowser
```

### 📌 Exercise 4.3.1: Square Calculator Socket Server
**Files:** `SquareServer.java`, `SquareClient.java`

TCP socket server that calculates squares of numbers:
- **Server Port:** 35000
- **Protocol:** Client sends number → Server responds with square
- **Features:** Concurrent client handling, input validation, error management

```bash
# Terminal 1 - Start Server
java -cp target/classes edu.eci.arsw.networking.SquareServer

# Terminal 2 - Start Client  
java -cp target/classes edu.eci.arsw.networking.SquareClient
```

### 📌 Exercise 4.3.2: Mathematical Function Server
**Files:** `MathFunctionServer.java`, `MathFunctionClient.java`,`MathFunctionDemo.java`

Advanced TCP server with dynamic function switching:
- **Default function:** Cosine
- **Supported functions:** sin, cos, tan
- **Dynamic switching:** `fun:sin`, `fun:cos`, `fun:tan` commands
- **Port:** 35001

```bash
# Terminal 1 - Start Server
java -cp target/classes edu.eci.arsw.networking.MathFunctionServer

# Terminal 2 - Start Client
java -cp target/classes edu.eci.arsw.networking.MathFunctionClient
```

### 📌 Exercise 4.5.1: Simple Web Server
**Files:** `SimpleWebServer.java`, `SimpleWebServerDemo.java`

HTTP/1.1 web server supporting multiple sequential requests:
- **Sequential processing:** Handles multiple non-concurrent requests
- **Static file serving:** HTML, CSS, JavaScript, images, and other files
- **MIME type detection:** Automatic content-type assignment
- **HTTP status codes:** 200 OK, 404 Not Found, 403 Forbidden, 500 Internal Server Error
- **Port:** 8081
- **Web root:** `webroot/` directory

```bash
# Start Web Server
java -cp target/classes edu.eci.arsw.networking.SimpleWebServer

# Test with Demo (in another terminal)
java -cp target/classes edu.eci.arsw.networking.SimpleWebServerDemo

# Access via browser: http://localhost:8081
```

### 📌 Exercise 5.2.1: UDP Time Server/Client
**Files:** `TimeServer.java`, `TimeClient.java`, `TimeDemo.java`

UDP-based time synchronization system:
- **Server Port:** 45000
- **Update Interval:** Every 5 seconds
- **Features:** Automatic reconnection, timeout handling, graceful degradation
- **Protocol:** Stateless UDP communication

```bash
# Terminal 1 - Start Time Server
java -cp target/classes edu.eci.arsw.networking.TimeServer

# Terminal 2 - Start Time Client
java -cp target/classes edu.eci.arsw.networking.TimeClient
```

### 📌 Exercise 6.4.1: RMI Chat Application
**Files:** `ChatService.java`, `RMIChatApplication.java`, `RMIChatDemo.java`

Bidirectional chat system using Java RMI:
- **Bidirectional communication:** Each instance acts as client and server
- **RMI Registry:** Automatic registry management
- **Features:** Real-time messaging, connection status, multiple participants
- **Demo modes:** Automatic, interactive, and manual configuration

```bash
# Interactive Demo
java -cp target/classes edu.eci.arsw.networking.RMIChatDemo interactive

# Manual Configuration
java -cp target/classes edu.eci.arsw.networking.RMIChatApplication
```

## 🛠️ Technologies Used

- **Java 17+** - Core programming language
- **Maven 3.6+** - Build and dependency management
- **Spring Boot 3.x** - Application framework
- **JUnit 5** - Unit testing framework
- **Java Networking APIs:**
  - `java.net.URL` - URL manipulation
  - `java.net.Socket` / `ServerSocket` - TCP communication
  - `java.net.DatagramSocket` - UDP communication  
  - `java.rmi.*` - Remote Method Invocation

## 📁 Project Structure

```
networking/
├── src/main/java/edu/eci/arsw/networking/
│   ├── URLAnalyzer.java              # Exercise 1: URL Analysis
│   ├── SimpleBrowser.java            # Exercise 2: HTTP Browser
│   ├── SquareServer.java             # Exercise 4.3.1: Square Calculator
│   ├── SquareClient.java
│   ├── MathFunctionServer.java       # Exercise 4.3.2: Function Server
│   ├── MathFunctionClient.java
│   ├── SimpleWebServer.java          # Exercise 4.5.1: Web Server
│   ├── SimpleWebServerDemo.java
│   ├── TimeServer.java               # Exercise 5.2.1: UDP Time Server
│   ├── TimeClient.java
│   ├── ChatService.java              # Exercise 6.4.1: RMI Chat
│   ├── RMIChatApplication.java
│   └── RMIChatDemo.java
├── src/test/java/                    # Comprehensive test suites
├── webroot/                          # Web server demo files
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## 🏁 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Network access for web-based exercises
- Ensure availability of ports 35000, 35001, 45000, and RMI registry ports

### Build the Project
```bash
git clone https://github.com/AnderssonProgramming/networking.git
cd networking
mvn clean compile
```

### Run All Tests
```bash
mvn test
```

## ▶️ Running the Exercises

Each exercise can be run independently. Most server-based exercises require starting the server first, then connecting clients.

### Quick Demo Commands
```bash
# Compile everything
mvn compile

# Exercise 1: URL Analysis
java -cp target/classes edu.eci.arsw.networking.URLAnalyzer

# Exercise 2: Simple Browser  
java -cp target/classes edu.eci.arsw.networking.SimpleBrowser

# Exercise 4: Socket Servers (requires 2 terminals)
java -cp target/classes edu.eci.arsw.networking.SquareServer
java -cp target/classes edu.eci.arsw.networking.MathFunctionServer

# Exercise 4.5.1: Web Server
java -cp target/classes edu.eci.arsw.networking.SimpleWebServer
# Then visit: http://localhost:8081

# Exercise 5: UDP Time System (requires 2 terminals)
java -cp target/classes edu.eci.arsw.networking.TimeServer
java -cp target/classes edu.eci.arsw.networking.TimeClient

# Exercise 6: RMI Chat Demo
java -cp target/classes edu.eci.arsw.networking.RMIChatDemo auto
```

## 🧪 Testing

The project includes comprehensive test suites for all components:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=URLAnalyzerTest
mvn test -Dtest=SquareServerTest  
mvn test -Dtest=MathFunctionServerTest
mvn test -Dtest=SimpleWebServerTest
mvn test -Dtest=TimeServerTest
mvn test -Dtest=RMIChatApplicationTest
```

**Test Coverage:**
- ✅ URL manipulation and validation
- ✅ HTTP client functionality  
- ✅ TCP socket communication
- ✅ HTTP server functionality
- ✅ UDP datagram protocols
- ✅ RMI distributed communication
- ✅ Error handling and edge cases
- ✅ Concurrent client scenarios

## 🏗️ Architecture Highlights

### Communication Patterns Demonstrated

1. **Request-Response** (HTTP, TCP Sockets)
   - Synchronous communication
   - Reliable, ordered delivery
   - Connection-oriented

2. **Datagram Communication** (UDP)
   - Asynchronous, connectionless
   - Lower overhead, faster transmission
   - Built-in timeout and retry mechanisms

3. **Remote Method Invocation** (RMI)
   - Distributed object communication
   - Transparent remote method calls
   - Object serialization and registry services

### Key Features Implemented

- **Concurrent Processing:** Multi-threaded servers handle multiple clients
- **Error Resilience:** Robust error handling with graceful degradation
- **Protocol Compliance:** Adherence to networking standards and best practices
- **Resource Management:** Proper cleanup of sockets, streams, and connections
- **Flexible Configuration:** Configurable ports, timeouts, and connection parameters

## 🎓 Learning Outcomes

This project demonstrates practical implementation of:

- **Network Programming Fundamentals:** Sockets, protocols, addressing
- **Client-Server Architecture:** Request-response patterns, stateful/stateless communication
- **Distributed Systems:** RMI, object serialization, service discovery
- **Protocol Implementation:** HTTP, TCP, UDP communication patterns
- **Error Handling:** Network timeouts, connection failures, graceful degradation
- **Concurrency:** Multi-threaded server design, resource sharing
- **Testing:** Network application testing strategies and frameworks

## 📝 Author

Implementation based on networking tutorials from the **Escuela Colombiana de Ingeniería - Arquitectura de Sofware**.
