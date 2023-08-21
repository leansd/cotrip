package cn.leansd.cotrip.model.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInit {
    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            repository.save(User.builder().name("Alice").build());
            repository.save(User.builder().name("Bob").build());
        };
    }
}