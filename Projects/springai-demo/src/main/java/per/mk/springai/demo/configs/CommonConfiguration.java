package per.mk.springai.demo.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class CommonConfiguration {

    @Bean
    public ChatMemory chatMemory() {
        return new TrackingChatMemory();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultSystem("你的名字叫阿坤。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    public static class TrackingChatMemory implements ChatMemory {
        private final Map<String, List<Message>> memory = new ConcurrentHashMap<>();
        private final Set<String> conversationIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

        @Override
        public void add(String conversationId, List<Message> messages) {
            memory.computeIfAbsent(conversationId, k -> new ArrayList<>()).addAll(messages);
            conversationIds.add(conversationId);
            log.info("ChatMemory 会话列表: {}", getConversationIds());
        }

        public List<Message> get(String conversationId) {
            return new ArrayList<>(memory.getOrDefault(conversationId, new ArrayList<>()));
        }

        public List<Message> get(String conversationId, int lastN) {
            List<Message> all = get(conversationId);
            if (all.size() <= lastN) {
                return all;
            }
            return all.subList(all.size() - lastN, all.size());
        }

        @Override
        public void clear(String conversationId) {
            memory.remove(conversationId);
            conversationIds.remove(conversationId);
        }

        public Set<String> getConversationIds() {
            return Set.copyOf(conversationIds);
        }
    }

}