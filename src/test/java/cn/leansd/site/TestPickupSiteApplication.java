package cn.leansd.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.leansd.base","cn.leansd.site"})
public class TestPickupSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestPickupSiteApplication.class, args);
	}

}