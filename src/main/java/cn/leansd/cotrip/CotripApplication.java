package cn.leansd.cotrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.leansd.cotrip", "cn.leansd.geo","cn.leansd.base"})
public class CotripApplication {

	public static void main(String[] args) {
		SpringApplication.run(CotripApplication.class, args);
	}

}