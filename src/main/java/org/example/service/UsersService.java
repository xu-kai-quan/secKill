package org.example.service;

import com.sun.org.apache.regexp.internal.RE;
import org.example.dao.UsersDao;
import org.example.entity.Users;
import org.example.exception.GlobalException;
import org.example.redis.RedisService;
import org.example.redis.UsersKey;
import org.example.result.CodeMsg;
import org.example.util.MD5Util;
import org.example.util.UUIDUtil;
import org.example.vo.LoginVo;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UsersService {

    public static final String COOKIE_NAME_TOKEN = "token";

    UsersDao usersDao;
    RedisService redisService;

    @Inject
    public UsersService(UsersDao usersDao, RedisService redisService) {
        this.usersDao = usersDao;
        this.redisService = redisService;
    }

    public Users getById(Long id) {
        return usersDao.getById(id);
    }

    public Users getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Users user = redisService.get(UsersKey.token, token, Users.class);
        //延长有效期
        if (user != null) {
            addCookie(response, token, user);
        }

        return user;
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        Users user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成Cookie
        String token = UUIDUtil.uuid(); //token-令牌
        addCookie(response, token, user);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, Users user) {

        redisService.set(UsersKey.token, token, user);//把私人信息存放到第三方的缓存当中
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UsersKey.token.expireSeconds());//cookie有效期
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
