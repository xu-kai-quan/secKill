package org.example.controller;

import com.mysql.cj.util.StringUtils;
import org.example.entity.User;
import org.example.redis.RedisService;
import org.example.redis.UserKey;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.UsersService;
import org.example.util.ValidatorUtil;
import org.example.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    UsersService usersService;
    RedisService redisService;

    @Inject
    public LoginController(UsersService usersService, RedisService redisService) {
        this.usersService = usersService;
        this.redisService = redisService;
    }

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        logger.info(loginVo.toString());
        //参数校验
//        String passInput = loginVo.getPassword();
//        String mobile = loginVo.getMobile();
//        if (StringUtils.isEmptyOrWhitespaceOnly(passInput)) {
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if (StringUtils.isEmptyOrWhitespaceOnly(mobile)) {
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        }
//        if (!ValidatorUtil.isMobile(mobile)) {
//            return Result.error(CodeMsg.MOBILE_ERROR);
//        }
        //登录
        String token = usersService.login(response, loginVo);
        return Result.success(token);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public boolean redisSet() {//UserKey:id1
        User user = new User(1, "1111111");
        return redisService.set(UserKey.getById, "" + 1, user);
    }

//    @RequestMapping("/db")
//    public User dbGet() {
//        User user = userService.getUserById(1);
//        return user;
//    }
//
//    @RequestMapping("/db/insert")
//    public boolean dbInsert() {
//        return userService.insertName();
//    }

    @RequestMapping("/hello")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }
}
