package com.example.life.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.sendRedirect("http://localhost:9010/login");
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/admin/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("http://localhost:9010/login")
                .defaultSuccessUrl("http://localhost:9004/admin/life", true)
                .permitAll()
                .and()
            .logout()
                .logoutUrl("http://localhost:9010/logout")
                .logoutSuccessUrl("http://localhost:9010/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("SESSION")
                .permitAll()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
            .csrf().disable()
            .sessionManagement()
                .maximumSessions(1);
    }
}
