package xyz.scootaloo.bootshiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BootShiroApplication {

	/**
	 * 修改JDK版本为14，对一些地方做了更改
	 * {@link "https://blog.csdn.net/m0_38001814/article/details/88831037"}
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(BootShiroApplication.class, args);
	}

}
