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

    /**
     * 返回聊天页面
     *
     * @return 视图名称
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 获取所有未删除的会话列表
     *
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public List<ChatSession> getSessions() {
        return chatHistoryService.getActiveSessions();
    }

    /**
     * 创建新会话
     *
     * @param body 请求体，包含会话名称(name)
     * @return 新会话的ID和名称
     */
    @PostMapping("/session")
    public Map<String, Object> createSession(@RequestBody(required = false) Map<String, String> body) {
        String name = body != null ? body.get("name") : null;
        ChatSession session = chatHistoryService.createSession(name);
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getSessionId());
        result.put("sessionName", session.getSessionName());
        return result;
    }

    /**
     * 更新会话名称
     *
     * @param sessionId 会话ID
     * @param body      请求体，包含新名称(name)
     * @return 操作结果
     */
    @PutMapping("/session/{sessionId}")
    public String updateSessionName(@PathVariable String sessionId, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        chatHistoryService.updateSessionName(sessionId, name);
        return "ok";
    }

    /**
     * 删除会话（逻辑删除）
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/{sessionId}")
    public String deleteSession(@PathVariable String sessionId) {
        chatHistoryService.deleteSession(sessionId);
        return "ok";
    }

    /**
     * 获取会话最近的10条消息
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/session/{sessionId}/messages")
    public List<ChatMessage> getSessionMessages(@PathVariable String sessionId) {
        return chatHistoryService.getRecentMessages(sessionId, 10);
    }

    /**
     * 流式聊天接口
     * - 如果没有传入sessionId，自动创建新会话
     * - 保存用户消息和AI回复到数据库
     * - 支持多轮对话，自动携带历史消息
     *
     * @param prompt    用户输入的问题
     * @param sessionId 会话ID（可选）
     * @return AI回复的流
     */
    @GetMapping(value = "/chat", produces = "text/plain;charset=UTF-8")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam(required = false) String sessionId) {
        // 如果没有会话ID，创建新会话
        if (sessionId == null || sessionId.isEmpty()) {
            ChatSession session = chatHistoryService.createSession(null);
            sessionId = session.getSessionId();
        }

        log.info("流式调用 用户问题: {}, sessionId: {}", prompt, sessionId);

        // 保存用户消息
        chatHistoryService.saveMessage(sessionId, "user", prompt);

        final String finalSessionId = sessionId;
        // 获取历史消息用于上下文
        List<Message> historyMessages = chatHistoryService.getMessagesForSession(sessionId, 20);

        StringBuilder fullResponse = new StringBuilder();

        // 根据是否有历史消息决定调用方式
        Flux<String> flux;
        if (historyMessages.isEmpty()) {
            // 首次对话
            flux = chatClient.prompt()
                    .user(prompt)
                    .stream()
                    .content();
        } else {
            // 多轮对话，携带历史上下文
            flux = chatClient.prompt()
                    .advisors(a -> a.param("chat_memory_conversation_id", finalSessionId))
                    .messages(historyMessages)
                    .user(prompt)
                    .stream()
                    .content();
        }

        // 流式返回的同时收集完整回复
        return flux.map(chunk -> {
            fullResponse.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            // 完成后保存AI回复
            if (fullResponse.length() > 0) {
                chatHistoryService.saveMessage(finalSessionId, "assistant", fullResponse.toString());
            }
        });
    }

}
