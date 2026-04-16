package per.mk.springai.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    @GetMapping("/")
    public String index() {
        return "index";
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
    public Flux<String> chat(@RequestParam(defaultValue = "讲个笑话") String prompt) {
        log.info("流式调用 用户问题: {}", prompt);
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }

}
