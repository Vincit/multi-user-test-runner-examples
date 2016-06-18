package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class TestMultiUserConfig extends AbstractMultiUserConfig<User, Role> {

    private static Map<String, User> users = new HashMap<>();

    @Autowired
    private UserService userService;

    public TestMultiUserConfig() {
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
    public User createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
        return userService.createUser(username, username, userRole);
    }

    @Override
    public Role stringToRole(String role) {
        return Role.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return users.get(username);
    }
}
