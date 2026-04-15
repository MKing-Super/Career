package per.mk.springai.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    private final ChatClient chatClient;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/ai/chat", produces = "text/event-stream;charset=UTF-8")
    @ResponseBody
    public Flux<String> chat(@RequestParam(defaultValue = "讲个笑话") String prompt) {
        log.info("用户问题: {}", prompt);
        return chatClient
                .prompt(prompt)
                .stream()
                .content();
    }

}
