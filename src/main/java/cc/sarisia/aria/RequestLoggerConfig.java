package cc.sarisia.aria;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggerConfig {

    @Bean
    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
        var logger = new CommonsRequestLoggingFilter();
        logger.setIncludeQueryString(true);
        return logger;
    }
}
