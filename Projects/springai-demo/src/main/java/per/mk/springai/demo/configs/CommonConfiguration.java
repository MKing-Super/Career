package per.mk.springai.demo.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 通用配置类
 * 配置 ChatClient
 */
@Configuration
@Slf4j
public class CommonConfiguration {

    /**
     * 配置 ChatClient Bean
     *
     * @param model OpenAI 聊天模型
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("你的名字叫阿坤。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

}
