package edu.uoc.tfg.pdfwebtools;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

import java.util.Locale;

@SpringBootApplication
@EnableCaching
public class PdfWebToolsApplication extends SpringBootServletInitializer{


    public static void main(String[] args) {
        SpringApplication.run(PdfWebToolsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PdfWebToolsApplication.class);
    }


    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(new Locale("en", "GB"));
    }


    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }

}
