package xyz.scootaloo.bootshiro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置
 * <a href="http://localhost:8080/doc.html">接口测试</a>
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 21:48
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean isEnable;

    private static final Contact DEFAULT_CONTACT =
            new Contact("fd", "http:www.scootaloo.xyz", "flutterdash@qq.com");

    @Bean
    public Docket Docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("BootShiro")
                .apiInfo(apiInfo())
                .enable(isEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("xyz.scootaloo.bootshiro.controller"))
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .contact(DEFAULT_CONTACT)
                .description("BootShiro")
                .title("权限管理，接口测试")
                .version("v0.1")
                .build();
    }

}
