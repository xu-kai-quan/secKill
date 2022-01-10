package org.example.controller;

import org.example.entity.Users;
import org.example.redis.RedisService;
import org.example.service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    UsersService usersService;
    RedisService redisService;

    @Inject
    public GoodsController(UsersService usersService, RedisService redisService) {
        this.usersService = usersService;
        this.redisService = redisService;
    }

    @RequestMapping("/to_list")
    public String toLogin(Model model, Users user
                          // @CookieValue(value = UsersService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                          //@RequestParam(value = UsersService.COOKIE_NAME_TOKEN, required = false) String paramToken
    ) {
        model.addAttribute("user", user);
        return "goods_list";
    }
}
