package irclog

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import irclog.security.GormUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.AntPathRequestMatcher
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver

@CompileStatic
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    GormUserDetailsService userDetailsService

    @Override
    void configure(WebSecurity web) {
        web.ignoring().antMatchers('/assets/**').antMatchers('/dbconsole/**')
    }

    @Override
    @CompileDynamic // somehow fails with CompileStatic
    void configure(HttpSecurity http) {
        http.exceptionHandling().accessDeniedPage('/login/denied')
            .and()
            .authorizeRequests().anyRequest().permitAll() // almost pages are visible
            .and()
            .formLogin().loginPage('/login').failureUrl('/login/authfail').permitAll()
            .and()
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).logoutSuccessUrl('/').permitAll() // use GET to trigger a logout (taking CSRF risk only for logout)
    }

    @Override
    void configure(AuthenticationManagerBuilder auth) {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        new BCryptPasswordEncoder()
    }

    @Bean
    SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        def mappingExceptionResolver = new SimpleMappingExceptionResolver()

        Properties mappings = new Properties()
        mappings.put(AccessDeniedException.name, '/login/denied')
        mappingExceptionResolver.exceptionMappings = mappings

        mappingExceptionResolver
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        def roleHierarchy = new RoleHierarchyImpl()
        roleHierarchy.hierarchy = "ROLE_ADMIN > ROLE_USER"
        roleHierarchy
    }
}
