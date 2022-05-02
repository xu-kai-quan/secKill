package org.example.controller;

import com.mysql.cj.util.StringUtils;
import org.example.entity.SecKillUser;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.UsersService;
import org.example.util.ValidatorUtil;
import org.example.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UsersController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
    UsersService usersService;

    @Inject
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @RequestMapping("/info")
    @ResponseBody
    public Result<SecKillUser> list(Model model, SecKillUser user) {
        System.out.println(user.getId());
        return Result.success(user);
    }

    @RequestMapping("/to_register")
    public String toRegister() {
        return "register";
    }

    @RequestMapping("/do_register")
    @ResponseBody
    public Result<String> doRegister(HttpServletResponse response, @Valid LoginVo loginVo) {
        logger.info(loginVo.toString());
//        参数校验
        String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if (StringUtils.isEmptyOrWhitespaceOnly(passInput)) {
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (StringUtils.isEmptyOrWhitespaceOnly(mobile)) {
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if (!ValidatorUtil.isMobile(mobile)) {
            return Result.error(CodeMsg.MOBILE_ERROR);
        }
        //注册
        usersService.register(response, loginVo);
        return Result.success("注册成功");
    }
}
