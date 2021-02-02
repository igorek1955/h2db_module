//package h2module.persistence.h2.configuration;
//
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Created by igor on 01 Feb, 2021
// */
//@Configuration
//public class WebConfiguration {
//    @Bean
//    ServletRegistrationBean h2servletRegistration(){
//        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
//        registration.addUrlMappings("/h2-console/*");
//        registration.addInitParameter("webAllowOthers", "true");
//        registration.addInitParameter("webPort", "7777");// <-- the port your wish goes here
//
//        return registration;
//    }
//}