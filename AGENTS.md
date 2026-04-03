# AGENTS.md - Development Guidelines

## Project Overview

This repository contains Java projects:
- **springcloud-demo**: Spring Cloud microservices demo (Java 8, Spring Boot 2.3.12.RELEASE, Spring Cloud Hoxton.SR12)
- **netty-demo**: Netty WebSocket server/client demo (Java 8, Netty 4.1.23.Final)

## Build Commands

### Spring Cloud Demo
```bash
# Navigate to project
cd springcloud-demo

# Build all modules
mvn clean install

# Build specific module
mvn clean install -pl work-service

# Run a module (from module directory)
mvn spring-boot:run

# Run single test
mvn test -Dtest=WorkServiceTest

# Package
mvn package -DskipTests
```

### Netty Demo
```bash
cd netty-demo

# Build
mvn clean package

# Run WebSocket server
mvn spring-boot:run
# Or directly: java -jar target/netty-demo-1.0-SNAPSHOT.jar
```

## Code Style Guidelines

### Naming Conventions
- **Classes**: PascalCase (e.g., `WorkService`, `NettyClient`)
- **Methods/variables**: camelCase (e.g., `getAllWorks`, `workMapper`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_TIMEOUT`)
- **Packages**: lowercase (e.g., `com.example.work.service`)

### Import Organization
1. Static imports
2. Java/Javax imports
3. Third-party imports (Spring, Netty, MyBatis, etc.)
4. Project imports

### Types & Annotations
- Use explicit types; avoid raw `List` without generics
- Use Spring annotations: `@Service`, `@RestController`, `@Autowired`, `@Repository`
- Use MyBatis annotations: `@Mapper`, `@Select`, `@Insert`, `@Update`, `@Delete`
- Use Lombok where applicable (check pom.xml for availability)

### Formatting
- 4-space indentation (no tabs)
- Line length: 120 characters max
- One blank line between methods
- Braces: same-line opening style (K&R)

### Error Handling
- Use try-catch for operations that may fail
- Return appropriate HTTP status codes in controllers
- Log exceptions with appropriate level (error for failures, debug for flow)
- Use meaningful error messages

### Controller Guidelines
- Use `@RestController` for REST APIs
- Use `@RequestMapping` or specific method annotations (`@GetMapping`, `@PostMapping`, etc.)
- Validate input with `@Valid` and Bean Validation annotations
- Return meaningful responses (not void for POST/PUT)

### Service Layer
- Business logic goes in `@Service` classes
- Inject dependencies via `@Autowired` or constructor injection
- Keep services focused and single-responsibility

### Mapper/Dao Layer
- Use MyBatis `@Mapper` interfaces
- Prefer annotations over XML for simple queries
- Use XML mapper files for complex queries

### Logging
- Use SLF4J (`Logger`, `LoggerFactory`)
- Log at appropriate levels: ERROR > WARN > INFO > DEBUG > TRACE
- Include context in log messages (e.g., user ID, request ID)

## Project Structure

```
springcloud-demo/
├── eureka-server/          # Service registry
├── api-gateway/            # Gateway service
├── work-service/           # Work microservice
├── work-admin/             # Work admin frontend
├── work-api/               # Work Feign API
├── life-service/           # Life microservice
├── life-admin/             # Life admin frontend
├── life-api/               # Life Feign API
├── home-service/           # Home microservice
├── home-admin/             # Home admin frontend
└── home-api/               # Home Feign API

netty-demo/
├── src/main/java/per/mk/pirate/netty/
│   ├── websocket/server/   # WebSocket server implementation
│   └── websocket/client/   # WebSocket client implementation
└── src/main/resources/     # Configuration files
```

## Common Tasks

### Adding a New Module
1. Create module directory under `springcloud-demo/`
2. Add module to parent `pom.xml` `<modules>` section
3. Create `pom.xml` with appropriate parent and dependencies

### Adding a New REST Endpoint
1. Create controller class with `@RestController`
2. Add request mapping annotation
3. Inject service via `@Autowired`
4. Implement endpoint logic

### Running Tests
```bash
# All tests
mvn test

# Single test class
mvn test -Dtest=ClassName

# Single test method
mvn test -Dtest=ClassName#methodName

# Skip tests
mvn clean install -DskipTests
```

## Notes
- No dedicated test framework configuration detected - add JUnit 5/4 to pom.xml for tests
- Configuration via `application.yml` in resources directory
- MyBatis mapper XML files in `src/main/resources/mapper/`
- HTML templates in `src/main/resources/templates/`

## Git Workflow
- After creating new files, always add them to git staging: `git add <file-paths>`
- Verify with `git status --short` to ensure new files show as `A` (staged)
- Exclude IDE files (`.idea`, `.iml`, `target`) from staging