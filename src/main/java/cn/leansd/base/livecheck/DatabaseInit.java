package cn.leansd.base.livecheck;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInit {
    @Bean
    CommandLineRunner initDatabase(FakeUserRepository repository) {
        return args -> {
            repository.save(FakeUser.builder().name("Alice").build());
            repository.save(FakeUser.builder().name("Bob").build());
        };
    }
}