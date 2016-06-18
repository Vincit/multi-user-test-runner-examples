package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

public class TestMultiRoleConfig extends AbstractMultiUserConfig<User, RoleGroup> {

    @Autowired
    private UserService userService;

    public TestMultiRoleConfig() {
    }

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(user);
    }

    @After
    public void tearDown() {
        userService.logout();
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
        return RoleGroup.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
