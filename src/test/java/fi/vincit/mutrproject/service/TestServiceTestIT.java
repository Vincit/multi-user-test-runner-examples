package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.AbstractConfiguredIT;

@TestUsers(creators = "role:ROLE_ADMIN", users = {"role:ROLE_ADMIN", "role:ROLE_USER"})
public class TestServiceTestIT extends AbstractConfiguredIT {

    @Autowired
    private TestService testService;

    @Test
    public void testName() throws Throwable {
        testService.getUsername();
        logInAs(LoginRole.USER);
        authorization().expect(call(testService::getAdmin)
                .toFail(ifAnyOf("role:ROLE_USER")));
    }
}
