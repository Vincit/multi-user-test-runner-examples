package fi.vincit.mutrproject.component.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.RoleGroup;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

public class TestConfiguration extends AbstractMultiUserConfig<User, RoleGroup> {

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    private UserService userService;

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(user);
    }

    @Override
    public User createUser(String username, String firstName, String lastName, RoleGroup userRole, LoginRole loginRole) {
        return userService.createUser(username, username, roleGroupToRoles(userRole));
    }

    private Collection<Role> roleGroupToRoles(RoleGroup roleGroup) {
        switch (roleGroup) {
            case ADMINISTRATOR: return Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_MODERATOR, Role.ROLE_USER);
            case REGULAR_USER: return Arrays.asList(Role.ROLE_USER);
            default: throw new IllegalArgumentException("Invalid role group " + roleGroup);
        }
    }

    @Override
    public RoleGroup stringToRole(String role) {
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }
}
