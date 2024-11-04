
# Kafka Microservices Project

This project demonstrates the use of Apache Kafka for event-driven communication between microservices in a Java-based application. The services use Kafka to publish and consume messages for efficient asynchronous communication.

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)
- [Microservices](#microservices)
- [Kafka Integration](#kafka-integration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Overview

This project contains a set of microservices that communicate via Apache Kafka. The event-driven architecture ensures loose coupling and scalability. The services include:
- **OrderService**: Responsible for placing orders.
- **ProductService**: Manages product details.
- **InventoryService**: Tracks product inventory.
  
Each service publishes and consumes messages through Kafka topics, enabling real-time updates and seamless data flow.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Apache Kafka**
- **MySQL** (for database)
- **Docker** (for containerization)
- **Spring Data JPA**

## Setup Instructions

### Prerequisites

Ensure you have the following installed on your machine:

- JDK 17
- Apache Kafka (locally or via Docker)
- MySQL
- Maven (optional, if you decide to switch to Maven)

### Clone the Repository

```bash
git clone git@github.com:priyansh-parmar-1/kafka.git
cd kafka
```

### Configure MySQL

1. Create a MySQL database named `microservice`.
2. Update the MySQL configuration in each service's `application.properties` file.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/microservice
spring.datasource.username=root
spring.datasource.password=your_password
```

### Start Kafka

Ensure Kafka is running on your machine. If you’re using Docker, you can start Kafka with:

```bash
docker-compose up
```

Or start Kafka locally using:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

### Running the Services

1. Start the Eureka server for service discovery.
2. Start each microservice by navigating to the respective project folder and running:

```bash
./mvnw spring-boot:run
```

Alternatively, you can run the JARs if you've packaged the applications:

```bash
java -jar target/<your-service-name>.jar
```

## Microservices

### OrderService

This service allows users to place orders. Each order generates a Kafka message, which contains the order details (order ID, product, quantity) and is sent to the `order-topic`.

### ProductService

This service handles product-related operations and listens to Kafka messages related to product updates.

### InventoryService

The InventoryService listens to Kafka messages to adjust product inventory based on the order placed.

## Kafka Integration

Kafka topics used:

- `order-topic`: Used by OrderService to send order details.
- `product-topic`: Used for managing product-related messages.
- `inventory-topic`: Used to update inventory when orders are placed.

Messages contain key information such as order ID, product ID, and quantity.

### Sample Kafka Message

```json
{
  "orderId": "12345",
  "productId": "67890",
  "quantity": 2
}
```

## Usage

1. Place an order via the OrderService.
2. Kafka will route the order details to ProductService and InventoryService.
3. The services will process the message and update their respective databases.

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request.

## License

This project is licensed under the MIT License.
