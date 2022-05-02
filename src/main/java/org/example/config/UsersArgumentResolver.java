package org.example.config;

import org.example.access.UserContext;
import org.example.entity.SecKillUser;
import org.example.service.UsersService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UsersArgumentResolver implements HandlerMethodArgumentResolver {

    UsersService usersService;

    @Inject
    public UsersArgumentResolver(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == SecKillUser.class;//如果遇到users参数才会继续处理,执行下面的方法
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
    }

}