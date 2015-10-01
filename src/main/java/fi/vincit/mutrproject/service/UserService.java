package fi.vincit.mutrproject.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    Optional<User> getLoggedInUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return Optional.of((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = repo.get(username);
        return new User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public User createUser(String username, String password, Role role) {
        return createUser(username, password, Arrays.asList(role));
    }

    public User createUser(String username, String password, Collection<Role> roles) {
        User user =  new User(username, password, roles);
        repo.put(username, user);
        return user;
    }

    public void clearUsers() {
        repo.clear();
    }

    public void loginUser(User user) {
        if (user != null) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
