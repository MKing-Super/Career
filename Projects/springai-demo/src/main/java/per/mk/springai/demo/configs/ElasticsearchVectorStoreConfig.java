package per.mk.springai.demo.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticsearchVectorStoreConfig {

    @Bean
    public ChatClient ragChatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, EmbeddingModel embeddingModel) {
        log.info("创建 RAG ChatClient，embeddingModel: {}", embeddingModel.getClass().getSimpleName());

        return chatClientBuilder
                .defaultSystem("你是阿坤，一个专业的问答助手，根据提供的上下文来回答用户的问题。如果上下文没有相关信息，请如实告知用户。")
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }
}