//package com.negah.dms.config;
//
//import AuthorizationMiddleware;
//import SessionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//
//import static Security.PUBLICS;
//import static Responses.forbidden;
//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//
//@Component
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SpringSecurityConfiguration {
//
//    private final SessionService sessionService;
//    private final AuthorizationMiddleware filter;
//
//    @Autowired
//    public SpringSecurityConfiguration(SessionService sessionService, AuthorizationMiddleware filter) {
//        this.sessionService = sessionService;
//        this.filter = filter;
//    }
//
//    public static String USERNAME;
//    public static String PASSWORD;
//
//    @Autowired
//    protected void globalConfiguration(
//            AuthenticationManagerBuilder authentication,
//            @Value("${spring.security.user.name}") String username,
//            @Value("${spring.security.user.password}") String password
//    ) throws Exception {
//        SpringSecurityConfiguration.USERNAME = username;
//        SpringSecurityConfiguration.PASSWORD = password;
//
//        if (Stream
//                .of(ofNullable(PASSWORD), ofNullable(USERNAME))
//                .allMatch(Optional::isPresent)) {
//
//            authentication
//                    .inMemoryAuthentication()
//                    .passwordEncoder(ENCODER)
//                    .withUser(username)
//                    .password(ENCODER.encode(password))
//                    .authorities(new ArrayList<>())
//                    .roles("USER", "ADMIN");
//        }
//        authentication
//                .userDetailsService(sessionService)
//                .passwordEncoder(ENCODER);
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    @Order(1)
//    public SecurityFilterChain api(HttpSecurity http) throws Exception {
//
//        PUBLICS.injectOn(http);
//
//        http
//                .antMatcher("/api/**")
//                .authorizeRequests()
//                .anyRequest().permitAll()
//                .and().csrf().disable();
//
//
//        http
//                .antMatcher("/api/**")
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .csrf()
//                .disable()
//                .exceptionHandling()
//                .authenticationEntryPoint((request, response, exception) -> forbidden(response))
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(STATELESS)
//                .and()
//                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
//                .cors()
//                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
//
//        return http.build();
//
//    }
//
//    @Bean
//    @Order(2)
//    public SecurityFilterChain app(HttpSecurity http) throws Exception {
//        http
//                .antMatcher("/app/**")
//                .authorizeRequests()
//                .antMatchers(GET, LOGIN_URL, "/app", "/app/register", "/app/recovery/**")
//                .permitAll()
//                .antMatchers(POST, "/app/register", "/app/recovery/**")
//                .permitAll()
//                .anyRequest()
//                .hasAuthority("USER")
//                .and()
//                .csrf()
//                .disable()
//                .formLogin()
//                .loginPage(LOGIN_URL)
//                .failureUrl(LOGIN_ERROR_URL)
//                .defaultSuccessUrl(HOME_URL)
//                .usernameParameter(USERNAME_PARAMETER)
//                .passwordParameter(PASSWORD_PARAMETER)
//                .and()
//                .rememberMe()
//                .key(TOKEN_SECRET)
//                .tokenValiditySeconds(DAY_MILLISECONDS)
//                .and()
//                .logout()
//                .deleteCookies(SESSION_COOKIE_NAME)
//                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL))
//                .logoutSuccessUrl(LOGIN_URL)
//                .and()
//                .exceptionHandling()
//                .accessDeniedPage(ACCESS_DENIED_URL);
//
//        return http.build();
//    }
//
//    @Bean
//    @Order(4)
//    public SecurityFilterChain swagger(HttpSecurity http) throws Exception {
//        if (Stream
//                .of(ofNullable(PASSWORD), ofNullable(USERNAME))
//                .allMatch(Optional::isPresent)) {
//
//            http
//                    .antMatcher("/swagger-ui/**")
//                    .authorizeRequests()
//                    .anyRequest()
//                    .authenticated()
//                    .and()
//                    .sessionManagement()
//                    .sessionCreationPolicy(STATELESS)
//                    .and()
//                    .httpBasic();
//        }
//
//        return http.build();
//    }
//}