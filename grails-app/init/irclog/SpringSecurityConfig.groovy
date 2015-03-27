package irclog

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl

@CompileStatic
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    GormUserDetailsService userDetailsService

    @Override
    void configure(HttpSecurity http) {
        http.authorizeRequests()
            .antMatchers('/channel/index/**').permitAll()
            .antMatchers('/channel/list/**').permitAll()
            .antMatchers('/channel/show/**').permitAll()
            .antMatchers('/channel/kick/**').hasRole("ADMIN")
            .antMatchers('/channel/**').hasRole("USER")
            .antMatchers('/register/create/**').permitAll()
            .antMatchers('/register/save/**').permitAll()
            .antMatchers('/register/**').hasRole("USER")
            .antMatchers('/person/**').hasRole("ADMIN")
            .antMatchers('/assets/**').permitAll()
            .antMatchers('**').permitAll()

        http.formLogin()
            .loginPage('/login')
            .failureUrl('/login?error')
            .permitAll()

        http.logout()
            .logoutUrl('/logout')
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
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl()
        roleHierarchy.hierarchy = "ROLE_ADMIN > ROLE_USER"
        return roleHierarchy
    }
}
