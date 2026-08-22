package dev.yuhanwang.relayrpc.example.consumer;

import dev.yuhanwang.relayrpc.example.common.model.User;
import dev.yuhanwang.relayrpc.example.common.service.UserService;

/**
 * Service consumer example
 */
public class ConsumerExample {
    public static void main(String[] args) {
        //TODO: replay with ServiceProxyFactory.getProxy(UserService.class) once proxy package exists
        UserService userService=new UserServiceProxy();

        User user = new User();
        user.setName("Blair");

        Long userId = userService.createUser(user);
        System.out.println("RPC 1: created user with ID "+userId);

        User result = userService.getUserById(userId);
        System.out.println("RPC 2: queried user from server: "+ result.getName()+" (ID: "+ result.getId()+")");


    }

}
