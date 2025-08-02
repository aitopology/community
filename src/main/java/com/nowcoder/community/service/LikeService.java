package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    public void like(int userId, int entityType, int entityId) {
        // 拼接key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 判断用户是否已经点赞
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember) {
            // 如果已经点赞，就取消点赞
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            // 如果没有点赞，就点赞
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    }

    public long findEntityLikeCount(int entityType, int entityId) {
        // 拼接key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        // 拼接key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 判断用户是否已经点赞
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
