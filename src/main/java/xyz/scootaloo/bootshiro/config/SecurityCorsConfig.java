package xyz.scootaloo.bootshiro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import xyz.scootaloo.bootshiro.utils.StringUtils;

import java.util.List;

/**
 * 解决跨域问题，参考:
 * {@link 'https://www.cnblogs.com/yuansc/p/9076604.html'}
 * @author : flutterdash@qq.com
 * @since : 2020年12月16日 15:25
 */
@Slf4j
@Configuration
public class SecurityCorsConfig {

    @Value("${allow-ip-address}")
    private String ipAddresses;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(getIpAddressList());
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config); // CORS 配置对所有接口都有效
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    public List<String> getIpAddressList() {
        List<String> ipAddress = StringUtils.splitBy(ipAddresses, ',');
        log.info("信任的站点 " + ipAddresses);
        return ipAddress;
    }

}
