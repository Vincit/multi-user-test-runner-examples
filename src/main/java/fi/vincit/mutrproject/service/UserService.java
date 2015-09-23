package fi.vincit.mutrproject.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fi.vincit.mutrproject.domain.Role;
import fi.vincit.mutrproject.domain.User;

@Service
public class UserService implements UserDetailsService {

    private Map<String, User> repo = new HashMap<>();

    @Override
    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return repo.get(username);
    }

    public User createUser(String username, Role role) {
        User user =  new User(username, username, Arrays.asList(role));
        repo.put(username, user);
        return user;
    }

    public void clearUsers() {
        repo.clear();
    }

    public void loginUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }
}
