package per.mk.springai.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/weather")
@Slf4j
@RequiredArgsConstructor
public class WeatherController {

    private final ChatClient weatherChatClient;
    private final ChatMemory weatherChatMemory;

    private final Map<String, List<Message>> sessionMessages = new ConcurrentHashMap<>();

    /**
     * 返回天气助手页面
     *
     * @return 视图名称
     */
    @GetMapping({"/", "/weather", "/weather/"})
    public String index() {
        return "weather";
    }

    /**
     * 流式聊天接口（天气助手）
     *
     * @param prompt    用户输入
     * @param sessionId 会话ID
     * @return AI回复流
     */
    @GetMapping(value = "/chat", produces = "text/plain;charset=UTF-8")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam(required = false) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
        }

        log.info("天气助手 - 用户问题: {}, sessionId: {}", prompt, sessionId);

        final String finalSessionId = sessionId;

        List<Message> messages = sessionMessages.computeIfAbsent(finalSessionId, k -> new ArrayList<>());
        messages.add(new UserMessage(prompt));

        StringBuilder fullResponse = new StringBuilder();

        Flux<String> flux = weatherChatClient.prompt()
                .advisors(new MessageChatMemoryAdvisor(weatherChatMemory))
                .advisors(a -> a.param("chat_memory_conversation_id", finalSessionId))
                .user(prompt)
                .stream()
                .content();

        return flux.map(chunk -> {
            fullResponse.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            if (fullResponse.length() > 0) {
                messages.add(new AssistantMessage(fullResponse.toString()));
            }
        });
    }

    /**
     * 清除会话
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @GetMapping("/clear")
    public String clearSession(@RequestParam String sessionId) {
        sessionMessages.remove(sessionId);
        weatherChatMemory.clear(sessionId);
        return "ok";
    }
}
