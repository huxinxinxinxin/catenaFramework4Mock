package com.catena.web.config;

import com.catena.core.CatenaContext;
import com.catena.core.CatenaControllerBase;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.web.interceptor.MockUrlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;

/**
 * Created by hx-pc on 16-5-17.
 */
@Configuration
public class MockConfig extends WebMvcConfigurationSupport {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(mockUrlInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/mock/**", "/favicon.ico", "/error")
                .excludePathPatterns("/api/park");
    }

    @Bean
    protected MockUrlInterceptor mockUrlInterceptor() {
        return new MockUrlInterceptor();
    }

    @Bean
    protected CatenaContext catenaContext() {
        return CatenaControllerBase.catenaContext;
    }

    @Bean
    public ScanUrlAndDataContext scanUrlAndDataContext() {
        try {
            return new ScanUrlAndDataContext();
        } catch (IOException e) {
            throw new MockRuntimeException("初始化ScanUrlAndDataContext失败", e, "500");
        }
    }

}
