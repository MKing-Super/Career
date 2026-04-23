package per.mk.springai.demo.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticsearchVectorStoreConfig {

    private final ChatMemory chatMemory = new InMemoryChatMemory();

    @Bean
    public ChatClient ragChatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, EmbeddingModel embeddingModel) {
        log.info("创建 RAG ChatClient，embeddingModel: {}", embeddingModel.getClass().getSimpleName());

        SearchRequest searchRequest = SearchRequest.builder()
                .topK(5)
                .similarityThreshold(0.3)
                .build();

        return chatClientBuilder
                .defaultSystem("你是阿坤，一个专业的问答助手。请严格按以下优先级回答：\n" +
                        "【最高优先级】如果提供了上下文（CONTEXT），必须基于上下文回答，禁止使用其他来源\n" +
                        "【次优先级】如果上下文信息不足或无关，先尝试根据上下文给出部分回答，再说明哪些信息来自你的知识\n" +
                        "【最后】如果完全没有上下文，才使用你自己的知识回答，但需说明是补充内容\n" +
                        "回答示例：\n" +
                        "有上下文：\"根据您提供的文档，答案是...\"\n" +
                        "无上下文：\"抱歉，我在您的文档库中没有找到相关信息，以下是基于我的知识库的回答...\"")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, searchRequest)
                )
                .build();
    }
}