package org.example.config;

import com.google.common.collect.Multiset;
import org.example.access.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    UsersArgumentResolver usersArgumentResolver;
    AccessInterceptor accessInterceptor;

    @Inject
    public WebConfig(UsersArgumentResolver usersArgumentResolver, AccessInterceptor accessInterceptor) {
        this.usersArgumentResolver = usersArgumentResolver;
        this.accessInterceptor = accessInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(usersArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor).addPathPatterns("/**");
    }
}
