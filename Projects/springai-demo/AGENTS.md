# AGENTS.md - springai-demo

## Project Overview
Spring Boot 3.4.5 project using Spring AI for LLM chat integration. Single-module Maven project.

## Build & Run
```bash
mvn clean package -DskipTests    # Build
java -jar target/springai-demo-0.0.1-SNAPSHOT.jar  # Run
mvn spring-boot:run              # Run with Maven
```

## Tech Stack
- Java 17
- Spring Boot 3.4.5, Spring AI 1.0.0-M6
- MyBatis Plus 3.5.10.1 (MySQL)
- Thymeleaf (templates/)

## Key Endpoints
- `GET /ai/` - UI index page
- `GET /ai/chat?prompt=xxx` - Streaming chat (SSE)
- `GET /ai/sync/chat?prompt=xxx` - Synchronous chat

## Config
- `src/main/resources/application.yml` - Main config (port 8090, AI API, datasource)
- AI configured for OpenAI-compatible API at `https://opencode.ai/zen`
- MySQL: `localhost:3306/home`, user `root`

## Project Structure
```
src/main/java/per/mk/springai/demo/
├── SpringaiDemoApplication.java   # Entry point
├── controller/ChatController.java # REST endpoints
└── configs/                       # Configuration classes

src/main/resources/
├── application.yml                # Main config
├── templates/index.html           # Thymeleaf UI
└── static/                        # Static assets
```
