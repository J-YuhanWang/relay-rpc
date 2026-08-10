package dev.yuhanwang.relayrpc.example.common.service;

import dev.yuhanwang.relayrpc.example.common.model.User;

public interface UserService {
    /**
     * get User
     * @return the user object
     */
    User getUserById(Long id);

    Long createUser(User user);
}
