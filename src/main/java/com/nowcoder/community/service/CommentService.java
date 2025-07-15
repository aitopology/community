package com.nowcoder.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    // 查询评论
    public List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }
    // 查询评论数量
    public int selectCountByEntity(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
}
