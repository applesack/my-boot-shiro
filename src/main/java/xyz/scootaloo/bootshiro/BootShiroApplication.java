package xyz.scootaloo.bootshiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BootShiroApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootShiroApplication.class, args);
	}

}
