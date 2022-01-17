package org.example.controller;

import org.example.entity.SecKillUser;
import org.example.result.Result;
import org.example.service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
@RequestMapping("/users")
public class UsersController {
    UsersService usersService;

    @Inject
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @RequestMapping("/info")
    @ResponseBody
    public Result<SecKillUser> list(Model model, SecKillUser user) {
        return Result.success(user);
    }
}
