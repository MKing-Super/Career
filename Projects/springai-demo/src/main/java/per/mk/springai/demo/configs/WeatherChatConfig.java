package per.mk.springai.demo.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import per.mk.springai.demo.tools.WeatherTools;

@Configuration
@Slf4j
public class WeatherChatConfig {

    @Bean
    public ChatMemory weatherChatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public ChatClient weatherChatClient(OllamaChatModel model, WeatherTools weatherTools) {
        return ChatClient.builder(model)
                .defaultSystem("你是一个专业、友好的天气助手，名叫\"小天\"。你的职责是：\n" +
                        "1. 回答用户关于天气的问题\n" +
                        "2. 当用户询问某城市的天气时，使用\"查询天气\"工具获取准确信息\n" +
                        "3. 当用户询问穿衣建议时，使用\"穿衣指数\"工具\n" +
                        "4. 当用户询问出行建议时，使用\"出行建议\"工具\n" +
                        "5. 用简洁、友好的语言回答，可以适当添加温馨提醒\n" +
                        "6. 如果用户没有明确说明城市，可以礼貌地询问")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(weatherTools)
                .build();
    }
}
