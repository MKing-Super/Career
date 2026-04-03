package com.example.admin.portal.config;

import com.example.admin.portal.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserFeign userFeign;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return true;
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Map<String, Object> result = userFeign.login(username, "");
                if (result == null || !(Boolean) result.getOrDefault("success", false)) {
                    throw new UsernameNotFoundException("User not found: " + username);
                }
                String role = (String) result.getOrDefault("role", "ADMIN");
                return User.builder()
                    .username(username)
                    .password("password")
                    .roles(role)
                    .build();
            }
        };
    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication authentication) throws org.springframework.security.core.AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();
                
                Map<String, Object> result = userFeign.login(username, password);
                
                if (result == null || !(Boolean) result.getOrDefault("success", false)) {
                    String message = (String) result.getOrDefault("message", "Invalid credentials");
                    throw new BadCredentialsException(message);
                }
                
                String role = (String) result.getOrDefault("role", "ADMIN");
                
                UserDetails user = User.builder()
                    .username(username)
                    .password(password)
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                    .build();
                
                return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
            }
            
            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(customAuthenticationProvider())
            .authorizeRequests()
                .antMatchers("/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/portal", true)
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("SESSION")
                .permitAll()
                .and()
            .csrf().disable()
            .sessionManagement()
                .maximumSessions(1);
    }
}
