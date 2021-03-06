package com.zengtong.Service;


import com.zengtong.Async.EventModel;
import com.zengtong.Async.EventProducer;
import com.zengtong.Async.EventType;
import com.zengtong.Utils.JedisAdaptor;
import com.zengtong.Utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {


    @Autowired
    private JedisAdaptor jedisAdaptor;

    @Autowired
    private EventProducer eventProducer;

    public long follow(int myId ,int entityID){
        if (isFollower(myId, entityID)){
            return getFolloweeCount(entityID);
        }
        jedisAdaptor.followTransaction(myId,entityID);
        eventProducer.fireEvent(new EventModel().setEventType(EventType.MESSAGE).setTo_id(entityID).setFrom_id(myId));
        return jedisAdaptor.zcard(RedisKeyUtil.getBizFollowlistKey(myId));
    }

    public long unFollow(int myId, int userId) {
        if (!isFollower(myId, userId))
            return getFollowerCount(userId);
        jedisAdaptor.unfollowTransaction(myId, userId);
        return getFolloweeCount(userId);
    }

    public long getFollowerCount(int entityId) {
        String followerKey = RedisKeyUtil.getBizFanslistKey(entityId);
        return jedisAdaptor.zcard(followerKey);
    }
    public List<Integer> getFollowers(int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getBizFanslistKey(entityId);
        return getIntegerIds(jedisAdaptor.zrevrange(followerKey, offset, offset+count));
    }

    public long getFolloweeCount(int userId) {
        String followeeKey = RedisKeyUtil.getBizFollowlistKey(userId);
        return jedisAdaptor.zcard(followeeKey);
    }


    public List<Integer> getFollowees(int userId, int offset, int count) {
        String followeeKey = RedisKeyUtil.getBizFollowlistKey(userId);

        return getIntegerIds(jedisAdaptor.zrevrange(followeeKey, offset, offset+count));
    }

    public boolean isFollower(int myID,int userID){
        String key = RedisKeyUtil.getBizFollowlistKey(myID);
        return jedisAdaptor.zismember(key, String.valueOf(userID));
    }

    private List<Integer> getIntegerIds(Set<String> ss) {
        List<Integer> listI = new ArrayList<>();
        for (String s : ss) {
            listI.add(Integer.parseInt(s));
        }
        return listI;
    }
}
