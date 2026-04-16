package per.mk.springai.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import per.mk.springai.demo.configs.CommonConfiguration.TrackingChatMemory;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final TrackingChatMemory chatMemory;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/new")
    @ResponseBody
    public String newChat() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @RequestMapping(value = "/sync/chat", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String syncChat(String prompt) {
        log.info("同步调用 用户问题: {}", prompt);
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @GetMapping(value = "/chat", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public Flux<String> chat(String prompt, @RequestParam(required = false) String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            chatId = UUID.randomUUID().toString().replace("-", "");
        }
        log.info("流式调用 用户问题: {}, chatId: {}", prompt, chatId);

        final String sessionId = chatId;
        return chatClient.prompt()
                .advisors(a -> a.param("chat_memory_conversation_id", sessionId))
                .user(prompt)
                .stream()
                .content();
    }

}
