package cn.leansd.base.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private UserSessionArgumentResolver userSessionArgumentResolver;
    public WebMvcConfig(@Autowired UserSessionArgumentResolver userSessionArgumentResolver){
        this.userSessionArgumentResolver = userSessionArgumentResolver;
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userSessionArgumentResolver);
    }
}
