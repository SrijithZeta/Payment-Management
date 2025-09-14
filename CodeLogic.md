# Payments Management System - Code Logic Documentation

## Project Overview

The Payments Management System is a secure internal application designed for fintech startups to track incoming and outgoing payments. The system implements role-based access control with three distinct user roles: Admin, Finance Manager, and Viewer.

## Architecture & Design Patterns

### 1. MVC (Model-View-Controller) Architecture

The application follows a clean MVC pattern:

- **Model**: Entity classes (`User`, `Payment`, `Report`, etc.) representing database tables
- **View**: Console-based UI handled by `UI.java` utility class
- **Controller**: Controllers for different user roles (`AdminController`, `ManagerController`, `ViewerController`)

### 2. Layered Architecture

```
┌─────────────────┐
│   Controllers   │ ← User interaction layer
├─────────────────┤
│    Services     │ ← Business logic layer
├─────────────────┤
│  Repositories   │ ← Data access layer
├─────────────────┤
│   Database      │ ← Data persistence layer
└─────────────────┘
```

### 3. SOLID Principles Implementation

- **Single Responsibility**: Each class has a single, well-defined purpose
- **Open/Closed**: Extensible through interfaces and inheritance
- **Liskov Substitution**: Repository interfaces allow implementation swapping
- **Interface Segregation**: Separate interfaces for different concerns
- **Dependency Inversion**: Services depend on repository interfaces, not implementations

## Core Business Logic

### 1. Authentication & Authorization

**Login Process:**
1. User provides username/password
2. System hashes password with BCrypt
3. Validates credentials against database
4. Returns user object with roles

**Role-Based Access Control:**
- **Admin**: Full system access, user management, role assignment
- **Finance Manager**: Payment management, report generation
- **Viewer**: Read-only access to payments and reports

### 2. Payment Management Workflow

**Payment Creation:**
1. Validate user has FINANCE_MANAGER or ADMIN role
2. Create PaymentDTO with required fields
3. Save to database with PENDING status
4. Log transaction in audit trail

**Status Updates:**
- PENDING → PROCESSING → COMPLETED
- Only authorized users can update status
- Each change is logged for audit purposes

### 3. Report Generation Logic

**Monthly Reports:**
1. Query payments for specific month
2. Calculate totals by direction (incoming/outgoing)
3. Generate formatted report
4. Save to file system and database

**Quarterly Reports:**
1. Aggregate data from 3 months
2. Calculate quarterly totals and trends
3. Generate comprehensive report
4. Store metadata for future reference

## Database Design

### Key Tables:
- `users`: User accounts and authentication
- `roles`: Available system roles
- `user_roles`: Many-to-many relationship
- `payments`: Payment transactions
- `reports`: Generated report metadata
- `audit_trail`: Complete transaction history

### Relationships:
- Users can have multiple roles
- Payments belong to users (created by)
- Reports reference payment data
- Audit trail tracks all changes

## Exception Handling Strategy

### Custom Exception Hierarchy:
```
RuntimeException
└── AppException (base custom exception)
    ├── AuthenticationException
    ├── DataAccessException 
    └── DuplicateEmailException
```

### Exception Usage:
- **AuthenticationException**: Authorization failures
- **DataAccessException**: Database operation errors
- **DuplicateEmailException**: Email uniqueness violations 

## Security Implementation

### 1. Password Security
- BCrypt hashing with salt
- No plain text password storage
- Secure password comparison

### 2. Input Validation
- SQL injection prevention through PreparedStatements
- Input sanitization in controllers
- Business rule validation in services

### 3. Audit Trail
- Complete transaction logging
- User action tracking
- Timestamp and user identification

## Testing Strategy

### Unit Tests:
- Service layer business logic
- Exception handling
- Data validation
- Authentication flows

### Test Coverage:
- JaCoCo integration for coverage reports
- Assertions for expected behavior
- Mock objects for external dependencies

## Performance Considerations

### 1. Database Optimization
- Indexed primary keys
- Efficient query patterns
- Connection pooling

### 2. Memory Management
- Proper resource cleanup
- Connection management
- ResultSet handling

## Error Handling & Logging

### Logging Levels:
- **INFO**: General application flow
- **ERROR**: Exception conditions
- **DEBUG**: Detailed debugging information

### Log Files:
- `logs/payments.log`: Main application log
- `logs/payments-YYYY-MM-DD.log`: Daily rotation

## GitHub Repository

**Repository URL**: [Your GitHub Repository Link]

## Setup Instructions for Evaluators

### Prerequisites:
1. Java 17 or higher
2. Maven 3.6+
3. PostgreSQL 12+

### Setup Steps:

1. **Clone Repository:**
```bash
git clone [repository-url]
cd MiniPPcursor
```

2. **Database Setup:**
```bash
# Create database
createdb minipp

# Run schema
psql -U postgres -d minipp -f src/main/resources/Tables.sql
```

3. **Configuration:**
   Update `src/main/resources/application.properties` with your database credentials.

4. **Build & Run:**
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.payments.Main"
```

5. **Run Tests:**
```bash
mvn test
mvn jacoco:report
```

### Default Test Data:
The system will create default admin user on first run. Use the signup feature to create additional test users.

## Key Features Demonstrated

1. **Clean Code**: Proper naming, comments, and structure
2. **SOLID Principles**: Modular, extensible design
3. **Security**: Role-based access, password hashing, audit trails
4. **Testing**: Comprehensive unit tests with assertions
5. **Documentation**: Clear code comments and documentation
6. **Error Handling**: Custom exceptions and proper error management

## Conclusion

The Payments Management System demonstrates enterprise-level Java development practices with clean architecture, comprehensive testing, and robust security features. The modular design allows for easy extension and maintenance while ensuring data integrity and user security.