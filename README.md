# Payments Management System

A secure internal Payments Management System for fintech startups to track incoming and outgoing payments with role-based access control.


## Checkout the working video [Click here for Drive link](https://drive.google.com/file/d/1glQyW63VGzvvWZC0LJqTVec8eNv_Dp9Y/view?usp=sharing)


## Features

- **Role-based Access Control**: Admin, Finance Manager, and Viewer roles
- **Payment Management**: Add, update, and track payment statuses
- **Financial Reporting**: Generate monthly and quarterly reports
- **Audit Trail**: Complete transaction history tracking
- **User Management**: User registration, authentication, and role assignment


##  System Architecture

### Design Patterns Used
- **MVC Pattern**: Clear separation of Model, View, and Controller
- **Repository Pattern**: Abstracted data access layer
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer objects for clean API design
- **Exception Hierarchy**: Structured error handling

## Technology Stack

- **Java 17+**
- **PostgreSQL Database**
- **Maven Build Tool**
- **JUnit 5** for testing
- **JaCoCo** for code coverage
- **BCrypt** for password hashing
- **SLF4J + Logback** for logging

## Prerequisites

- Java 17 or higher (knowledge in OOPs concepts)
- Maven 
- PostgreSQL 
- Git

## Setup Instructions

### 1. Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE YOUR_DB_NAME;
```

2. Update database configuration in `src/main/resources/application.properties`:
```properties
db.url=jdbc:postgresql://localhost:5434/YOUR_DB_NAME;
db.username=postgres
db.password=****
```

3. Run the SQL script to create tables:
```bash
psql -U postgres -d minipp -f src/main/resources/Tables.sql
```

### 2. Build and Run

1. Clone the repository:
```bash
git clone <my-github-repo-url>
cd Payment-Management
```

2. Build the project:
```bash
mvn clean compile
```

3. Run the application:


    For Admin Login please use this Credentials  
    Username: admin  
    Password: admin
```bash
mvn exec:java -Dexec.mainClass="com.payments.Main"
```

### 3. Run Tests

```bash
# Run all tests
mvn test

# Generate code coverage report
mvn jacoco:report
```

The coverage report will be generated in `target/site/jacoco/index.html`

## Usage

### Default Users
- **Admin**: Can manage users, assign roles, and access all features
- **Finance Manager**: Can manage payments and generate reports
- **Viewer**: Can view payments and reports (read-only)

### Core Workflows

1. **User Registration**: New users are assigned VIEWER role by default
2. **Role Assignment**: Admin can promote users to FINANCE_MANAGER or ADMIN
3. **Payment Recording**: Finance Managers can add incoming/outgoing payments
4. **Status Updates**: Track payment status (Pending → Processing → Completed)
5. **Report Generation**: Generate monthly/quarterly financial reports

## Project Structure

```
src/
├── main/java/com/payments/
│   ├── Application.java          # Main application entry point
│   ├── config/                   # Database configuration
│   ├── controller/               # MVC Controllers
│   ├── dto/                      # Data Transfer Objects
│   ├── exception/                # Custom exceptions
│   ├── model/                    # Entity models
│   ├── repository/               # Data access layer
│   ├── service/                  # Business logic layer
│   └── util/                     # Utility classes
├── test/java/com/payments/       # Unit tests
└── resources/
    ├── application.properties    # Configuration
    ├── Tables.sql               # Database schema
    └── logback.xml              # Logging configuration
```

## API Endpoints - CLI (Console-based)

The application provides a console-based interface with the following main menus:

- **Authentication Menu**: Login/Signup
- **Admin Menu**: User management, role assignment
- **Finance Manager Menu**: Payment management, report generation
- **Viewer Menu**: Read-only access to payments and reports

## Testing

The project includes comprehensive unit tests covering:

- User authentication and authorization
- Payment creation and updates
- Exception handling
- Business logic validation

Run tests with: `mvn test`

## Code Coverage

JaCoCo is configured to generate code coverage reports. View the report at:
`target/site/jacoco/index.html`

## Logging

Application logs are written to:
- Console output
- `logs/payments.log` file uses SF4J logging framework with Logback configuration
- `logs/payments-YYYY-MM-DD.log` (daily rotation)
- `MonthlyReports/monthly_report_YYYY_ID.txt` (monthly report logs)
- `QuaterlyReports/quarterly_report_2025_Q[1-4].txt` (quarterly report logs)


