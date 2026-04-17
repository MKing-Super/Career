package per.mk.springai.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;
import per.mk.springai.demo.entity.ChatMessage;
import per.mk.springai.demo.entity.ChatSession;
import per.mk.springai.demo.service.ChatHistoryService;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final ChatHistoryService chatHistoryService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/sessions")
    public List<ChatSession> getSessions() {
        return chatHistoryService.getActiveSessions();
    }

    @PostMapping("/session")
    public Map<String, Object> createSession(@RequestBody(required = false) Map<String, String> body) {
        String name = body != null ? body.get("name") : null;
        ChatSession session = chatHistoryService.createSession(name);
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getSessionId());
        result.put("sessionName", session.getSessionName());
        return result;
    }

    @PutMapping("/session/{sessionId}")
    public String updateSessionName(@PathVariable String sessionId, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        chatHistoryService.updateSessionName(sessionId, name);
        return "ok";
    }

    @DeleteMapping("/session/{sessionId}")
    public String deleteSession(@PathVariable String sessionId) {
        chatHistoryService.deleteSession(sessionId);
        return "ok";
    }

    @GetMapping("/session/{sessionId}/messages")
    public List<ChatMessage> getSessionMessages(@PathVariable String sessionId) {
        return chatHistoryService.getRecentMessages(sessionId, 10);
    }

    @GetMapping(value = "/chat", produces = "text/plain;charset=UTF-8")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam(required = false) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            ChatSession session = chatHistoryService.createSession(null);
            sessionId = session.getSessionId();
        }

        log.info("流式调用 用户问题: {}, sessionId: {}", prompt, sessionId);

        chatHistoryService.saveMessage(sessionId, "user", prompt);

        final String finalSessionId = sessionId;
        List<Message> historyMessages = chatHistoryService.getMessagesForSession(sessionId, 20);

        StringBuilder fullResponse = new StringBuilder();

        Flux<String> flux;
        if (historyMessages.isEmpty()) {
            flux = chatClient.prompt()
                    .user(prompt)
                    .stream()
                    .content();
        } else {
            flux = chatClient.prompt()
                    .advisors(a -> a.param("chat_memory_conversation_id", finalSessionId))
                    .messages(historyMessages)
                    .user(prompt)
                    .stream()
                    .content();
        }

        return flux.map(chunk -> {
            fullResponse.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            if (fullResponse.length() > 0) {
                chatHistoryService.saveMessage(finalSessionId, "assistant", fullResponse.toString());
            }
        });
    }

}
