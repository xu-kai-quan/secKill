package org.example.config;

import com.google.common.collect.Multiset;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    UsersArgumentResolver usersArgumentResolver;

    @Inject
    public WebConfig(UsersArgumentResolver usersArgumentResolver) {
        this.usersArgumentResolver = usersArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(usersArgumentResolver);
    }

}
