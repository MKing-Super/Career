package per.mk.springai.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import per.mk.springai.demo.entity.ChatMessage;

/**
 * 消息 Mapper 接口
 * 继承 MyBatis Plus BaseMapper
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
