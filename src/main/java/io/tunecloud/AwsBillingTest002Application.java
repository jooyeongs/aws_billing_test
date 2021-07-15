package io.tunecloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ComponentScan
@Configuration
//@EnableAutoConfiguration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AwsBillingTest002Application {

	public static void main(String[] args) {
		SpringApplication.run(AwsBillingTest002Application.class, args);
	}
    @Bean
    public InternalResourceViewResolver setupViewResolver() {
 
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
 
        resolver.setPrefix("/templates/saas/");
        resolver.setSuffix(".HTML");
        return resolver;
    }
}
