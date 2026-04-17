# AGENTS.md - springai-demo

## Project Overview
Spring Boot 3.4.5 project using Spring AI for LLM chat integration with MySQL persistence.

## Build & Run
```bash
mvn clean package -DskipTests    # Build
java -jar target/springai-demo-3.4.5.jar  # Run
mvn spring-boot:run              # Run with Maven
```

## Tech Stack
- Java 17
- Spring Boot 3.4.5, Spring AI 1.0.0-M6
- MyBatis Plus 3.5.10.1 (MySQL)
- Thymeleaf (templates/)

## Database
- MySQL: `localhost:3306/home`, user `root`, password `123`
- Run SQL in `src/test/java/per/mk/springai/demo/database_schema.sql` to create tables

## Key Endpoints
- `GET /ai/` - Chat UI page
- `GET /ai/sessions` - Get all active sessions
- `POST /ai/session` - Create new session
- `PUT /ai/session/{sessionId}` - Update session name
- `DELETE /ai/session/{sessionId}` - Delete session (logical)
- `GET /ai/session/{sessionId}/messages` - Get last 10 messages
- `GET /ai/chat?prompt=xxx&sessionId=xxx` - Streaming chat

## Database Tables
### ai_chat_session
- `id`, `session_id`, `session_name`, `is_deleted`, `create_time`, `update_time`

### ai_chat_message
- `id`, `session_id`, `role` (user/assistant), `content`, `create_time`

## Config
- `src/main/resources/application.yml` - Main config (port 8090, AI API, datasource)
- AI configured for OpenAI-compatible API at `https://opencode.ai/zen`
- **API Key**: Set via environment variable `OPENAI_API_KEY` or input at startup

## Run
```bash
java -jar target/springai-demo-3.4.5.jar --spring.ai.openai.api-key=your_api_key
```

## Project Structure
```
src/main/java/per/mk/springai/demo/
├── SpringaiDemoApplication.java   # Entry point
├── controller/ChatController.java # REST endpoints
├── entity/                        # MyBatis Plus entities
├── mapper/                        # MyBatis Plus mappers
├── service/ChatHistoryService.java # Session & message service
└── configs/                      # Configuration classes

src/test/java/per/mk/springai/demo/
└── database_schema.sql            # Database schema
```
