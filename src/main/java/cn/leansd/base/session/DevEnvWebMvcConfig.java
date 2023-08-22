package cn.leansd.base.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Profile("dev")
public class DevEnvWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private DevUserSessionArgumentResolver userSessionArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userSessionArgumentResolver);
    }
}
