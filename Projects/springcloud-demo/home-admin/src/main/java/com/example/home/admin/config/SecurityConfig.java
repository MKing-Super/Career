package com.example.home.admin.config;

import com.example.home.admin.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SessionService sessionService;

    @Bean
    public SessionFilter sessionFilter() {
        return new SessionFilter();
    }

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
                .antMatchers("/css/**", "/js/**", "/admin/**", "/redis/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("http://localhost:9010/login")
                .defaultSuccessUrl("http://localhost:9006/admin/home", true)
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("http://localhost:9010/login?logout")
                .permitAll()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                .and()
            .addFilterBefore(sessionFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    public static class SessionFilter extends OncePerRequestFilter {

        @Autowired
        private SessionService sessionService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            Cookie cookie = getSessionCookie(request);
            if (cookie != null) {
                String sessionData = sessionService.getUsername(cookie.getValue());
                if (sessionData != null) {
                    String role = sessionService.getRole(cookie.getValue());
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        sessionData, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "ADMIN")))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);
        }

        private Cookie getSessionCookie(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("MK_SESSION".equals(cookie.getName())) {
                        return cookie;
                    }
                }
            }
            return null;
        }
    }
}
