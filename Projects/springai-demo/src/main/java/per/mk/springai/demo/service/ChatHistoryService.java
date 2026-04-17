package per.mk.springai.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import per.mk.springai.demo.entity.ChatMessage;
import per.mk.springai.demo.entity.ChatSession;
import per.mk.springai.demo.mapper.ChatMessageMapper;
import per.mk.springai.demo.mapper.ChatSessionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;

    public ChatSession createSession(String name) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        session.setSessionName(name != null && !name.isEmpty() ? name : "新会话");
        sessionMapper.insert(session);
        return session;
    }

    public List<ChatSession> getActiveSessions() {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getIsDeleted, 0)
                .orderByDesc(ChatSession::getUpdateTime);
        return sessionMapper.selectList(wrapper);
    }

    public ChatSession getSessionById(String sessionId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getIsDeleted, 0)
                .eq(ChatSession::getSessionId, sessionId);
        return sessionMapper.selectOne(wrapper);
    }

    public void updateSessionName(String sessionId, String name) {
        LambdaUpdateWrapper<ChatSession> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatSession::getSessionId, sessionId)
                .set(ChatSession::getSessionName, name);
        sessionMapper.update(null, wrapper);
    }

    public void deleteSession(String sessionId) {
        LambdaUpdateWrapper<ChatSession> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatSession::getSessionId, sessionId)
                .set(ChatSession::getIsDeleted, 1);
        sessionMapper.update(null, wrapper);
    }

    public void saveMessage(String sessionId, String role, String content) {
        ChatSession session = getSessionById(sessionId);
        if (session == null) {
            session = createSession(null);
        }

        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        messageMapper.insert(message);

        log.info("保存消息: sessionId={}, role={}, content={}", sessionId, role, content);
    }

    public List<Message> getMessagesForSession(String sessionId, int limit) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit);
        List<ChatMessage> messages = messageMapper.selectList(wrapper);

        List<Message> result = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage msg = messages.get(i);
            if ("user".equals(msg.getRole())) {
                result.add(new UserMessage(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
                result.add(new AssistantMessage(msg.getContent()));
            }
        }
        return result;
    }

    public List<ChatMessage> getRecentMessages(String sessionId, int limit) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit);
        List<ChatMessage> messages = messageMapper.selectList(wrapper);
        java.util.Collections.reverse(messages);
        return messages;
    }
}
