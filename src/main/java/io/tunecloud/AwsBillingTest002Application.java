package io.tunecloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

//@Configuration
@EnableAutoConfiguration
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
//@ComponentScan
@SpringBootApplication
@MapperScan("io.tunecloud.potal.site.**.dao")
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
