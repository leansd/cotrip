package cn.leansd.cotrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"cn.leansd"})
@EntityScan(basePackages = {"cn.leansd"})
@EnableJpaRepositories(basePackages = {"cn.leansd"})
public class CotripApplication {

	public static void main(String[] args) {
		SpringApplication.run(CotripApplication.class, args);
	}

}