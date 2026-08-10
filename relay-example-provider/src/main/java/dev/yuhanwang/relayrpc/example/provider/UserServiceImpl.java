package dev.yuhanwang.relayrpc.example.provider;

import dev.yuhanwang.relayrpc.example.common.model.User;
import dev.yuhanwang.relayrpc.example.common.service.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserServiceImpl implements UserService {
    private static final Map<Long,User> store = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public User getUserById(Long id) {
        return store.get(id);
    }

    @Override
    public Long createUser(User user) {
        Long id = idGenerator.incrementAndGet();
        user.setId(id);
        store.put(id,user);
        return id;
    }
}
