package com.example.work.admin.config;

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
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String SESSION_PREFIX = "mk:session:";
    private static final long SESSION_TIMEOUT = 2 * 60 * 60;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

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
                .antMatchers("/css/**", "/js/**", "/admin/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("http://localhost:9010/login")
                .defaultSuccessUrl("http://localhost:9002/admin/work", true)
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

    public class SessionFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            Cookie cookie = getSessionCookie(request);
            if (cookie != null) {
                String key = SESSION_PREFIX + cookie.getValue();
                String value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    String username = value.split(":")[0];
                    String role = value.length() > 1 ? value.split(":")[1] : "ADMIN";
                    redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
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
