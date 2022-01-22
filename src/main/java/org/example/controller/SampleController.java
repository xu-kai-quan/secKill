package org.example.controller;

import org.example.entity.User;
import org.example.rabbitmq.MQSender;
import org.example.redis.RedisService;
import org.example.redis.UserKey;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@Controller
@RequestMapping("/demo")
public class SampleController {
    UserService userService;
    RedisService redisService;
    MQSender mqSender;

    @Inject
    public SampleController(UserService userService, RedisService redisService, MQSender mqSender) {
        this.userService = userService;
        this.redisService = redisService;
        this.mqSender = mqSender;
    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq() {
//        mqSender.send("hello mq");
//        return Result.success("hello hello");
//    }


    @RequestMapping("/redis")
    @ResponseBody
    public User redisGet() {
        return redisService.get(UserKey.getById, "" + 1, User.class);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public boolean redisSet() {//UserKey:id1
        User user = new User(1, "1111111");
        return redisService.set(UserKey.getById, "" + 1, user);
    }

    @RequestMapping("/db")
    public User dbGet() {
        User user = userService.getUserById(1);
        return user;
    }

    @RequestMapping("/db/insert")
    public boolean dbInsert() {
        return userService.insertName();
    }

    @RequestMapping("/hello")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }

    public Result<Object> error() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }
}
