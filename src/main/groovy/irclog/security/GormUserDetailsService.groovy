package irclog.security

import grails.transaction.Transactional
import irclog.Person
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class GormUserDetailsService implements UserDetailsService {

    @Transactional(readOnly = true)
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        def person = Person.where { loginName == username }.get()
        if (!person) {
            throw new UsernameNotFoundException("Username not found: ${username}")
        }
        new User(person.loginName, person.password, person.roles.collect { new SimpleGrantedAuthority(it.name) })
    }
}
