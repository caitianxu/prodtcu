package com.cjsz.tech.core;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cjsz.tech.core.ext.MJFastJsonHttpMessageConverter;
import freemarker.template.utility.XmlEscape;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.servlet.MultipartConfigElement;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * webMvc配置
 *
 * webMvc层面只扫描@Controller的类
 *
 * AutoConfigureAfter：在xxx配置后
 */
@Configuration
@ComponentScan(basePackages = "com.cjsz.tech", useDefaultFilters = false, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class}),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {ControllerAdvice.class})})
@AutoConfigureAfter(PlatformConfig.class)

public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(WebMvcConfig.class);

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // /**
    // * 视图设置
    // */
    // @Bean(name = "viewResolver")
    // public InternalResourceViewResolver viewResolver() {
    // InternalResourceViewResolver resolver = new
    // InternalResourceViewResolver();
    // resolver.setPrefix("/WEB-INF/views/jsp/");
    // resolver.setSuffix(".jsp");
    // resolver.setViewClass(JstlView.class);
    // resolver.setOrder(2);
    // return resolver;
    // }

    @Bean(name = "freeMarkerViewResolver")
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setSuffix(".html");
        resolver.setPrefix("");
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setCache(false);
        resolver.setRequestContextAttribute("request");
        resolver.setExposeRequestAttributes(true);
        resolver.setExposePathVariables(true);
        resolver.setExposeSessionAttributes(true);
        resolver.setAllowRequestOverride(true);
        resolver.setAllowSessionOverride(true);
        resolver.setOrder(1);
        return resolver;
    }

    @Bean(name = "freeMarkerConfigurer")
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPaths("WEB-INF/views","static");
        configurer.setPreferFileSystemAccess(false);
        Map<String, Object> freemarkerVariables = new HashMap<String, Object>();
        freemarkerVariables.put("xml_escape", new XmlEscape());
        configurer.setFreemarkerVariables(freemarkerVariables);
        Properties settings = new Properties();
        settings.setProperty("auto_import", "include/spring.ftl as spring");
        settings.setProperty("default_encoding", "UTF-8");
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("number_format", "#.##");
        configurer.setFreemarkerSettings(settings);
        return configurer;
    }

    @Bean
    public MessageSource messageSource() {
        PathMatchingResourceBundleMessageSource messageSource = new PathMatchingResourceBundleMessageSource();
        messageSource.setBasenames("classpath*:/i18n/messages*", "classpath*:/META-INF/message*");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    //
    // @Bean(name = "multipartResolver")
    // public CommonsMultipartResolver commonsMultipartResolver() {
    // CommonsMultipartResolver multipartResolver = new
    // CommonsMultipartResolver();
    // multipartResolver.setMaxUploadSize(52428800);
    // multipartResolver.setMaxInMemorySize(52428800);
    // return multipartResolver;
    // }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(524288000);
        factory.setMaxRequestSize(524288000);
        return factory.createMultipartConfig();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 支持GET\POST等
//		registry.addInterceptor(new SasAllowOriginInterceptor());
//		registry.addWebRequestInterceptor(new DDSHoderInterceptor());
		registry.addWebRequestInterceptor(new VersionInterceptor("development"));
//		registry.addInterceptor(new ParameterInterceptor());
    }

    @Bean
    public HttpMessageConverters customConverters() {
//        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        MJFastJsonHttpMessageConverter fastJsonHttpMessageConverter = new MJFastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setSupportedMediaTypes(
                Arrays.asList(new MediaType("application", "json", Charset.forName("UTF-8")),
                        MediaType.valueOf("application/javascript;charset=UTF-8"), MediaType.ALL));
        fastJsonHttpMessageConverter.setFeatures(SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty);
        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }


    // ---------注册filter和servlet
    // @Bean(name = "springFilterChain")
    // public FilterRegistrationBean springFilterChain() {
    // FilterRegistrationBean bean = new FilterRegistrationBean();
    // bean.setFilter(new DelegatingFilterProxy());
    // bean.addUrlPatterns("/*");
    // bean.setOrder(1);
    // return bean;
    // }

    // @Bean(name = "captchaServlet")
    // public ServletRegistrationBean captchaServlet() {
    // ServletRegistrationBean bean = new ServletRegistrationBean();
    // bean.setServlet(new CaptchaServlet());
    // bean.addUrlMappings("/captcha");
    // return bean;
    // }

}
