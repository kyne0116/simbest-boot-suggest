# Simbest Boot Suggest

A Spring Boot application with the following specifications:

- **Group ID**: com.simbest.boot
- **Artifact ID**: suggest
- **Version**: 0.1
- **Spring Boot Version**: 2.2.2.RELEASE
- **Java Version**: 1.8

## Project Structure

```
simbest-boot-suggest/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── simbest/
│   │   │           └── boot/
│   │   │               └── suggest/
│   │   │                   ├── SimbestApplication.java
│   │   │                   └── controller/
│   │   │                       └── HelloController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Getting Started

### Prerequisites

- JDK 1.8
- Maven 3.x

### Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080. You can access the sample endpoint at:

```
http://localhost:8080/hello
```

## Building the Application

```bash
mvn clean package
```

This will create a JAR file in the `target` directory that can be run with:

```bash
java -jar target/suggest-0.1.jar
```
