# FutureFunds Investment Planner

## Overview
FutureFunds is a simulation project designed to help users plan and manage their investments. It allows users to create, update, and delete investment plans, as well as generate summary reports for their investments.

## Features
- User registration and authentication
- Investment plan management
- Goal tracking
- Notification system
- Investment simulation
- Progress tracking

## Technologies Used
- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- MySQL
- Maven
- Lombok
- JWT for authentication
- iText for PDF generation

## Project Structure
- `src/main/java/com/cg/futurefunds/`
  - `controller/`: REST API endpoints
  - `model/`: Data models
  - `service/`: Business logic
  - `repository/`: Data access layer
  - `dto/`: Data Transfer Objects
  - `config/`: Configuration classes
  - `utility/`: Utility classes
  - `exceptions/`: Custom exceptions

## API Endpoints

### User Management
- `POST /user`: Register a new user
- `POST /user/login`: User login
- `PUT /user/forgot`: Forgot password
- `PUT /user/reset`: Reset password
- `PUT /user/change`: Change password
- `PUT /user/update`: Update user details

### Investment Management
- `POST /investments`: Add a new investment
- `PUT /investments/{id}`: Update an investment
- `DELETE /investments/{id}`: Delete an investment
- `GET /investments/user`: Get all investments for a user
- `GET /investments/{id}`: Get a specific investment
- `GET /investments/simulate`: Simulate an investment
- `GET /investments/{id}/progress`: Get investment progress
- `GET /investments/pay/{id}`: Make a payment for an investment

### Goal Management
- `POST /goals`: Add a new goal
- `PUT /goals/{id}`: Update a goal
- `DELETE /goals/{id}`: Delete a goal
- `GET /goals/user/{userid}`: Get all goals for a user
- `GET /goals/{id}`: Get a specific goal

### Notification Management
- `GET /notifications`: Get all notifications for a user

## Configuration
The application uses environment variables for configuration:
- `DB_URL`: MySQL database URL
- `DB_USERNAME`: MySQL username
- `DB_PASSWORD`: MySQL password
- `USER_MAIL`: Email for notifications
- `USER_PASSWORD`: Email password

## Getting Started
1. Clone the repository
2. Set up the environment variables
3. Run `mvn spring-boot:run` to start the application

## Testing
The project includes basic tests in `src/test/java/com/cg/futurefunds/FuturefundsApplicationTests.java`.

## License
This project is licensed under the MIT License.
