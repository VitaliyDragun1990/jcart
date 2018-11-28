package com.revenat.jcart.admin.config;

import com.revenat.jcart.admin.security.PostAuthorizationFilter;
import com.revenat.jcart.admin.web.converters.RoleCommandToRoleConverter;
import com.revenat.jcart.admin.web.converters.RoleToRoleCommandConverter;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.Filter;
import java.util.Locale;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("${server.port:9443}")
    private int serverPort;

    @Autowired
    private MessageSource messageSource;


    /**
    * This bean set order for Spring Security filter manually so that you can place your own
    * filter after this one. See {@code http://stackoverflow.com/questions/25957879/filter-order-in-spring-bo}
    * */
    @Bean
    @Autowired
    public FilterRegistrationBean securityFilterChain(
            @Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)Filter securityFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(securityFilter);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        return registration;
    }

    /**
     * This bean registers custom filter whose job is to add appropriate request attribute for properly
     * managing user interactions with menu items (which menu is active at the moment)
     * */
    @Bean
    @Autowired
    public FilterRegistrationBean postAuthorizationFilterRegistrationBean(PostAuthorizationFilter postAuthorizationFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(postAuthorizationFilter);
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

    /**
     * This bean creates Thymeleaf email template resolver to enable ability to load templates which reside in classpath
     * resource using class loader. With specified context these templates used for sending emails.
     * */
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("email-templates/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding("UTF-8");
        emailTemplateResolver.setOrder(2);

        return emailTemplateResolver;
    }

    /**
    * This bean responsible for resolving user's locale by session attribute.
    * */
    @Bean
    public LocaleResolver localResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }

    /**
    * This bean responsible for registering  user's locale change by using request parameter.
    * */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
    * This bean responsible for registration specific Thymeleaf dialect for spring security tasks.
    * */
    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }

    /**
     * This bean configures Tomcat's EmbeddedServletContainerFactory to redirect from HTTP to HTTPS
     * */
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        
        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    private Connector initiateHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(9090);
        connector.setSecure(false);
        connector.setRedirectPort(serverPort);

        return connector;
    }


    /*
    * Enables ability to use message source from i18n for storing validation messages.
    * */
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }


    /**
     * Programmatically declare simple view controllers and theirs mappings.
     * */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("public/login");
        registry.addRedirectViewController("/", "/home");
    }

    /**
     * Allows add custom converters to registry so that we can use inject {@code ConversionService} into our components
     * and use it like {@code conversionService.convert(source, targetType)}.
     * */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addConverter(new RoleToRoleCommandConverter());
        registry.addConverter(new RoleCommandToRoleConverter());
    }
}
