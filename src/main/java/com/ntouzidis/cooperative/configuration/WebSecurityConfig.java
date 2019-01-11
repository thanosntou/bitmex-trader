package com.ntouzidis.cooperative.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Properties;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
//        auth.jdbcAuthentication().dataSource(securityDataSource);
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // use jdbc authentication ... oh yeah!!!
//        auth.jdbcAuthentication().dataSource(securityDataSource);
//        auth.userDetailsService(userDetailsService);
//    }

//     @Bean
//    public UserDetailsManager userDetailsManager() {
//        CustomUserDetailsManager detailsManager = new CustomUserDetailsManager();
////        jdbcUserDetailsManager.setDataSource(securityDataSource);
////        jdbcUserDetailsManager.setEnableAuthorities(true);
//        return detailsManager;
//    }



//    @Bean
//    public Authentication authentication() throws Exception {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials());
//    }
//
//    @Bean(name="authenticationManager")
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    // Expose the UserDetailsService as a Bean
//    @Bean
//    @Override
//    public UserDetailsService userDetailsServiceBean() throws Exception {
//        return super.userDetailsServiceBean();
//    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**").allowedOrigins("http://localhost:4000");
//            }
//        };
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
//        http.authorizeRequests()
//                .antMatchers("/welcome/**").permitAll()
////                .antMatchers("/**").hasAnyRole("CUSTOMER", "MEMBER", "ADMIN")
//                .antMatchers("/dashboard/**").hasAnyRole("CUSTOMER", "TRADER", "ADMIN")
//                .antMatchers("/trade/**").hasAnyRole("TRADER", "ADMIN")
//                .antMatchers("/copy/**").hasAnyRole("CUSTOMER", "ADMIN")
////                .antMatchers("/user/**").hasAnyRole("CUSTOMER", "TRADER", "ADMIN")
//                .antMatchers("/resources/**").permitAll()
//                .antMatchers("/management-panel/**").hasAnyRole("MEMBER", "ADMIN")
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/offers/**").hasRole("MEMBER")
//                .antMatchers("/order/**").denyAll()
////                .antMatchers("/order/**").hasRole("CUSTOMER")
//                .antMatchers("/cart/**").hasRole("CUSTOMER")
//                .antMatchers("/email/**").hasRole("ADMIN")
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/authenticateTheUser")
//                .permitAll()
//                .and()
//                .logout().permitAll()
//                .and()
//                .exceptionHandling().accessDeniedPage("/access-denied");
//
//                http.formLogin().defaultSuccessUrl("/proxy", true);
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.office365.com");
        mailSender.setPort(25);
        mailSender.setUsername("admin@bitmexcallbot.com");
        mailSender.setPassword("kobines4ever//");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        mailSender.setUsername("thanosntouzidis@gmail.com");
//        mailSender.setPassword("azsfasfasf");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");
//
//        return mailSender;
//    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                user.withDefaultPasswordEncoder()
//                        .username("user")
//                        .password("password")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }


}
