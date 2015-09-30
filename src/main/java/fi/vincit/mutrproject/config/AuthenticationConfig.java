package fi.vincit.mutrproject.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;

import fi.vincit.mutrproject.domain.Role;
import fi.vincit.mutrproject.service.UserService;

@Configuration
public class AuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @PostConstruct
    public void initUsers() {
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user", "user", Role.ROLE_USER);
    }
}
