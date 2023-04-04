package com.ankit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class MyConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
        .authorizeHttpRequests().requestMatchers("/admin/**")
        .hasRole("ADMIN").
        and()
        .authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER")
        .and().authorizeHttpRequests().anyRequest().permitAll()
        .and().formLogin().loginPage("/signin")
        .loginProcessingUrl("/dologin")
        .defaultSuccessUrl("/user/index");
        
        //loginPage for custom login page
        
        //AuthenticationManager
        httpSecurity.authenticationProvider(authenticationProvider());
        
        return httpSecurity.build();

    }
    
    @Bean
    AuthenticationManager authenticationManager( AuthenticationConfiguration configuration ) throws Exception {
    	return configuration.getAuthenticationManager();
    	
    }
    
    @Bean
    UserDetailsService getUserDetailsService() {
    	return new UserDetailServiceImpl();
    }
    
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
    
    DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
    }
    
}
