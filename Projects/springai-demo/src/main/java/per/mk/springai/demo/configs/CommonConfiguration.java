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

/**
 * Spring AI 通用配置类
 * 配置 ChatClient 和 ChatMemory
 */
@Configuration
@Slf4j
public class CommonConfiguration {

    /**
     * 配置 ChatMemory Bean
     * 使用自定义的 TrackingChatMemory 实现
     *
     * @return ChatMemory 实例
     */
    @Bean
    public ChatMemory chatMemory() {
        return new TrackingChatMemory();
    }

    /**
     * 配置 ChatClient Bean
     *
     * @param model      OpenAI 聊天模型
     * @param chatMemory 聊天记忆组件
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultSystem("你的名字叫阿坤。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    /**
     * 自定义 ChatMemory 实现
     * 继承 Spring AI 的 ChatMemory 接口
     * 额外功能：追踪所有会话ID并打印日志
     */
    public static class TrackingChatMemory implements ChatMemory {
        private final Map<String, List<Message>> memory = new ConcurrentHashMap<>();
        private final Set<String> conversationIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

        /**
         * 添加消息到指定会话
         *
         * @param conversationId 会话ID
         * @param messages       消息列表
         */
        @Override
        public void add(String conversationId, List<Message> messages) {
            memory.computeIfAbsent(conversationId, k -> new ArrayList<>()).addAll(messages);
            conversationIds.add(conversationId);
            log.info("ChatMemory 会话列表: {}", getConversationIds());
        }

        /**
         * 获取指定会话的所有消息
         *
         * @param conversationId 会话ID
         * @return 消息列表
         */
        public List<Message> get(String conversationId) {
            return new ArrayList<>(memory.getOrDefault(conversationId, new ArrayList<>()));
        }

        /**
         * 获取指定会话最近N条消息
         *
         * @param conversationId 会话ID
         * @param lastN           返回的消息数量
         * @return 消息列表
         */
        public List<Message> get(String conversationId, int lastN) {
            List<Message> all = get(conversationId);
            if (all.size() <= lastN) {
                return all;
            }
            return all.subList(all.size() - lastN, all.size());
        }

        /**
         * 清除指定会话的所有消息
         *
         * @param conversationId 会话ID
         */
        @Override
        public void clear(String conversationId) {
            memory.remove(conversationId);
            conversationIds.remove(conversationId);
        }

        /**
         * 获取所有会话ID
         *
         * @return 会话ID集合
         */
        public Set<String> getConversationIds() {
            return Set.copyOf(conversationIds);
        }
    }

}
