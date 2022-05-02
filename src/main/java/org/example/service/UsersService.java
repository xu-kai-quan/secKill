package org.example.service;

import org.example.dao.UsersDao;
import org.example.entity.SecKillUser;
import org.example.exception.GlobalException;
import org.example.redis.KeyPrefix;
import org.example.redis.RedisService;
import org.example.redis.UserKey;
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
import java.util.Random;

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

    public SecKillUser getById(Long id) {
        //取缓存
        SecKillUser user = redisService.get(UsersKey.getById, "" + id, SecKillUser.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = usersDao.getById(id);
        if (user != null) {
            redisService.set(UsersKey.getById, "" + id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        SecKillUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        SecKillUser toBeUpdate = new SecKillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        usersDao.updatePassword(toBeUpdate);
        //处理缓存
        redisService.delete(UsersKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UsersKey.token, token, user);
        return true;
    }

    public SecKillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SecKillUser user = redisService.get(UsersKey.token, token, SecKillUser.class);//对象缓存
        //延长有效期
        if (user != null) {
            addCookie(response, token, user);
        }

        return user;
    }

    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SecKillUser user = getById(Long.parseLong(mobile));
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
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, SecKillUser user) {

        redisService.set(UsersKey.token, token, user);//把私人信息存放到第三方的缓存当中
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UsersKey.token.expireSeconds());//cookie有效期
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void register(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SecKillUser user = getById(Long.parseLong(mobile));
        if (user != null) {
            throw new GlobalException(CodeMsg.MOBILE_EXIST);
        }
        //存到数据库中
        SecKillUser users = new SecKillUser();
        users.setPassword(MD5Util.inputPassToFormPass(formPass));
        Random random = new Random();
        users.setNickname("a" + random.nextInt(26) + "a" + random.nextInt(26));
        users.setSalt("1a2b3c4d");
        users.setId(Long.parseLong(mobile));
        usersDao.insertNewUser(users);
    }

}
