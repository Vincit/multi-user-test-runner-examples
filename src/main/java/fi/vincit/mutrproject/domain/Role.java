package fi.vincit.mutrproject.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SUPER_ADMIN,
    ROLE_MODERATOR,
    ROLE_ANONYMOUS;

    @Override
    public String getAuthority() {
        return name();
    }
}
