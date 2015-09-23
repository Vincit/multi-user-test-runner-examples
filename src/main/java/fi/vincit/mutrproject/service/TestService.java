package fi.vincit.mutrproject.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fi.vincit.mutrproject.domain.User;

@Service
public class TestService {

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String getUsername() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAdmin() {
        return "Foo";
    }
}
