package com.cydeo.config;

import com.cydeo.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final SecurityService securityService;
    private final AuthSuccessHandler authSuccessHandler;


    // this is for hard coded users may be used for testing purposes.
    // overrides Spring User object :
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//
//        List<UserDetails> userList = List.of(
//                new User("mike", encoder.encode("Abc1"),
//                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))),    // ROLE_ADMIN : spring standard starts with ROLE_
//
//                new User("ozzy", encoder.encode("Abc1"),
//                        List.of(new SimpleGrantedAuthority("ROLE_MANAGER")))
//        );
//
//        return new InMemoryUserDetailsManager(userList);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeRequests()
//                .antMatchers("/user/**").hasRole("ADMIN")   // everything under user controller should be accessible by "admin" role.
//                .antMatchers("/project/**").hasRole("MANAGER")  // it concatenates ROLE_ later that's why we cannot use it with our entity
//                .antMatchers("/task/employee/**").hasRole("EMPLOYEE")
//                .antMatchers("/task/**").hasRole("MANAGER")
//                .antMatchers("/task/**").hasAnyRole("EMPLOYEE", "ADMIN")          // ROLE_ since it concatenates it later
//                .antMatchers("/task/**").hasAuthority("ROLE_EMPLOYEE")  // needs ROLE_ to use spring default settings
                    .antMatchers("/user/**").hasAuthority("Admin")   // everything under user controller should be accessible by "Admin" role.
                    .antMatchers("/project/**").hasAuthority("Manager") // if we don't restrict any page & directory, it can be reached by any logged-in user
                    .antMatchers("/task/employee/**").hasAuthority("Employee")
                    .antMatchers(               // everybody without log in should see these :
                        "/",
                        "/login",
                        "/fragments/**",    // everything inside fragments directory
                        "/assets/**",
                        "/images/**"
                        ).permitAll()   // anybody should access these pages, directories
                    .anyRequest().authenticated()
                .and()
//                .httpBasic()      // we want to use spring pop-up form which comes after our login form
                    .formLogin()
                        .loginPage("/login")
//                        .defaultSuccessUrl("/welcome")
                        .successHandler(authSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()    // anybody should access login form
                .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                .and()
                    .rememberMe()
                    .tokenValiditySeconds(120)
                    .key("cydeo")   // any key can be written
                    .userDetailsService(securityService)    // we send our securityService to remember which user
                .and()
                .build();
    }
}
