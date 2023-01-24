package webServer.config;

import database.APIKeyDao;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import webServer.userManagement.UserDetailServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailServiceImpl userDetailsService() {
        return new UserDetailServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilterFilterRegistrationBean(APIKeyDao apiKeyDao) {
        FilterRegistrationBean<ApiKeyAuthFilter> authFilter = new FilterRegistrationBean<>();

        authFilter.setFilter(new ApiKeyAuthFilter(apiKeyDao));

        authFilter.addUrlPatterns(ApiKeyAuthFilter.pathsToFilterFor);

        return authFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    String[] staticResources  =  {
            "/css/**",
            "/gifs/**",
            "/images/**",
            "/js/**",
            "/favicon.ico"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.userDetailsService(userDetailsService())
            .authorizeRequests().antMatchers(staticResources).permitAll()
            .and()
            .authorizeRequests().antMatchers("/admin/**" , "/actuator/**").hasAnyRole("ADMIN")
            .and()
            .authorizeRequests().antMatchers("/user/**").hasAnyRole("USER")
            .and()
            .authorizeRequests().antMatchers("/", "/twoFactorAuthentication", "/login/**", "/logout/**", "/whatIsForDinner/**", "/dinner/**", "/register/**", "/vinmonopolet/**", "/words/**", "/sprites/**", "/rest/**").permitAll()
            .and()
            .authorizeRequests().antMatchers("/**").denyAll()
            .and()
            .formLogin().loginPage("/login").usernameParameter("username").passwordParameter("password").permitAll()
            .loginProcessingUrl("/doLogin")
            .successForwardUrl("/login/postLogin")
            .failureUrl("/login/loginFailed")
            .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login/logout").permitAll()
            .and()
            .exceptionHandling().accessDeniedPage("/accessDenied")
            .and()
            .csrf().disable();

    }
}
