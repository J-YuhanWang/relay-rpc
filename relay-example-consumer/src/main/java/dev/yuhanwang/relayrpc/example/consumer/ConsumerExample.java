package dev.yuhanwang.relayrpc.example.consumer;

import dev.yuhanwang.relayrpc.example.common.model.User;
import dev.yuhanwang.relayrpc.example.common.service.UserService;

/**
 * Service consumer example
 */
public class ConsumerExample {
    public static void main(String[] args) {
        //TODO: replay with ServiceProxyFactory.getProxy(UserService.class) once proxy package exists
        UserService userService=null;

        User user = new User();
        user.setName("Blair");

        // TODO: uncomment once userService is a real proxy
//        Long id = userService.createUser(user);
//        User result = userService.getUserById(id);
//        System.out.println(result.getName());


    }

}
