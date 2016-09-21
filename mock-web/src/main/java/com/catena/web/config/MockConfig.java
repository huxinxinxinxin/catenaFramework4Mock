package com.catena.web.config;

import com.catena.core.CatenaContext;
import com.catena.core.CatenaControllerBase;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.mock.inject.InjectionLoading;
import com.catena.web.interceptor.MockUrlConditionalInterceptor;
import com.lyncode.jtwig.mvc.JtwigViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;

/**
 * Created by hx-pc on 16-5-17.
 */
@Configuration
public class MockConfig extends WebMvcConfigurationSupport {


    @Value ("${spring.twig.cache:true}")
    private Boolean twigCache;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(mockUrlConditionalInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/init")
                .excludePathPatterns("/api/mock", "/favicon.ico", "/error");
    }

    @Bean
    protected MockUrlConditionalInterceptor mockUrlConditionalInterceptor() {
        return new MockUrlConditionalInterceptor();
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
            throw new MockRuntimeException(499, "初始化ScanUrlAndDataContext失败");
        }
    }

    @Bean
    public InjectionLoading getInjectionLoading() {
        return new InjectionLoading();
    }


    @Bean
    public ViewResolver viewResolver() {
        JtwigViewResolver viewResolver = new JtwigViewResolver();
        viewResolver.setPrefix("classpath:html/");
        viewResolver.setSuffix(".html");
        viewResolver.setEncoding("UTF8");
        viewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        if (twigCache == Boolean.FALSE) {
            viewResolver.setCached(false);
        }
        viewResolver.configuration().render();
        return viewResolver;
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}
