package per.mk.springai.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RAG 控制器
 * 提供文档上传、向量存储、对���检索功能
 */
@Controller
@Slf4j
public class RagController {

    private final ChatMemory chatMemory = new InMemoryChatMemory();

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatClient ragChatClient;

    /**
     * RAG 主页面
     */
    @GetMapping("/rag")
    public String ragIndex() {
        return "rag";
    }

    /**
     * 对话页面
     */
    @GetMapping("/rag/chat/index")
    public String ragChatPage() {
        return "rag-chat";
    }

    /**
     * 上传文档到向量库
     * 将文件分块后存入 Elasticsearch 向量存储
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/rag/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空";
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String filename = file.getOriginalFilename();

            log.info("[阿坤] 上传文件: {}, 大小: {} bytes", filename, content.length());

            List<org.springframework.ai.document.Document> documents = new ArrayList<>();
            List<String> chunks = chunkText(content, 500);

            for (int i = 0; i < chunks.size(); i++) {
                org.springframework.ai.document.Document doc = org.springframework.ai.document.Document.builder()
                        .id(UUID.randomUUID().toString())
                        .text(chunks.get(i))
                        .metadata("source", filename)
                        .metadata("chunk", String.valueOf(i + 1))
                        .metadata("total", String.valueOf(chunks.size()))
                        .build();
                documents.add(doc);
            }

            vectorStore.add(documents);

            log.info("[阿坤] 已成功导入 {} 个文档块到向量库", documents.size());
            return "成功导入 " + documents.size() + " 个文档块";

        } catch (IOException e) {
            log.error("[阿坤] 读取文件失败", e);
            return "读取文件失败: " + e.getMessage();
        } catch (Exception e) {
            log.error("[阿坤] 导入向量库失败", e);
            return "导入失败: " + e.getMessage();
        }
    }

    /**
     * 流式对话（带记忆）
     * 使用内存存储会话历史，支持多轮对话，同时检索向量库
     *
     * @param prompt  用户提问
     * @param sessionId 会话ID，用于区分不同会话的记忆
     * @return AI 回答
     */
    @RequestMapping(value = "/rag/chat", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String chatStream(@RequestParam("prompt") String prompt,
                            @RequestParam(value = "sessionId", required = false) String sessionId) {
        log.info("[阿坤] 提问: {}", prompt);

        String session = (sessionId != null && !sessionId.isEmpty()) ? sessionId : "default";
        StringBuilder fullResponse = new StringBuilder();

        ragChatClient.prompt()
                .user(prompt)
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .advisors(a -> a.param("chat_memory_conversation_id", session))
                .stream()
                .content()
                .doOnNext(fullResponse::append)
                .doOnComplete(() -> log.info("[阿坤] 回答: {}", fullResponse))
                .doOnError(e -> log.error("[阿坤] 对话失败", e))
                .collectList()
                .block();

        return fullResponse.toString();
    }

    /**
     * 获取向量库文档数量
     */
    @GetMapping("/rag/count")
    @ResponseBody
    public long countDocuments() {
        return 0;
    }

    /**
     * 清空向量库
     */
    @GetMapping("/rag/clear")
    @ResponseBody
    public String clearVectorStore() {
        try {
            vectorStore.delete("SELECT * FROM vector_docs");
            log.info("[阿坤] 已清空向量库");
            return "已清空向量库";
        } catch (Exception e) {
            log.error("[阿坤] 清空向量库失败", e);
            return "清空失败: " + e.getMessage();
        }
    }

    /**
     * 清理会话记忆
     *
     * @param sessionId 会话ID
     */
    @DeleteMapping("/rag/memory")
    @ResponseBody
    public String clearMemory(@RequestParam("sessionId") String sessionId) {
        chatMemory.clear(sessionId);
        log.info("[阿坤] 已清空会话记忆: {}", sessionId);
        return "已清空会话: " + sessionId;
    }

    /**
     * 文本分块
     * 按换行符分割，每块最大500字符
     *
     * @param text     原始文本
     * @param chunkSize 每块最大字符数
     */
    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        String[] paragraphs = text.split("\n");

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) {
                continue;
            }

            if (paragraph.length() <= chunkSize) {
                chunks.add(paragraph);
            } else {
                int start = 0;
                while (start < paragraph.length()) {
                    int end = Math.min(start + chunkSize, paragraph.length());
                    chunks.add(paragraph.substring(start, end));
                    start = end;
                }
            }
        }

        return chunks;
    }
}