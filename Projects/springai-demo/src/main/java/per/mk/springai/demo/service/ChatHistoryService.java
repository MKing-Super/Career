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

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;

    /**
     * 创建新会话
     *
     * @param name 会话名称，如果为空则使用默认名称"新会话"
     * @return 创建的会话对象
     */
    public ChatSession createSession(String name) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        session.setSessionName(name != null && !name.isEmpty() ? name : "新会话");
        sessionMapper.insert(session);
        return session;
    }

    /**
     * 获取所有未删除的会话列表
     *
     * @return 会话列表，按修改时间倒序排列
     */
    public List<ChatSession> getActiveSessions() {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getIsDeleted, 0)
                .orderByDesc(ChatSession::getUpdateTime);
        return sessionMapper.selectList(wrapper);
    }

    /**
     * 根据会话ID获取会话信息
     *
     * @param sessionId 会话唯一标识
     * @return 会话对象，如果不存在返回null
     */
    public ChatSession getSessionById(String sessionId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getIsDeleted, 0)
                .eq(ChatSession::getSessionId, sessionId);
        return sessionMapper.selectOne(wrapper);
    }

    /**
     * 更新会话名称
     *
     * @param sessionId 会话唯一标识
     * @param name      新的会话名称
     */
    public void updateSessionName(String sessionId, String name) {
        LambdaUpdateWrapper<ChatSession> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatSession::getSessionId, sessionId)
                .set(ChatSession::getSessionName, name);
        sessionMapper.update(null, wrapper);
    }

    /**
     * 逻辑删除会话
     *
     * @param sessionId 会话唯一标识
     */
    public void deleteSession(String sessionId) {
        LambdaUpdateWrapper<ChatSession> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatSession::getSessionId, sessionId)
                .set(ChatSession::getIsDeleted, 1);
        sessionMapper.update(null, wrapper);
    }

    /**
     * 保存聊天消息到数据库
     *
     * @param sessionId 会话唯一标识
     * @param role      消息角色 (user/assistant)
     * @param content   消息内容
     */
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

    /**
     * 获取会话的历史消息（用于发送给AI）
     *
     * @param sessionId 会话唯一标识
     * @param limit     返回的消息数量限制
     * @return 消息列表，按时间正序排列
     */
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

    /**
     * 获取会话最近的N条消息（用于前端展示）
     *
     * @param sessionId 会话唯一标识
     * @param limit     返回的消息数量限制
     * @return 消息列表，按时间正序排列
     */
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
