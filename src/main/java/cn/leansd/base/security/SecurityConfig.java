package cn.leansd.base.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@Profile("default")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // 允许H2控制台的所有路径
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable();
        http.oauth2ResourceServer().jwt();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        return http.build();
    }
}
