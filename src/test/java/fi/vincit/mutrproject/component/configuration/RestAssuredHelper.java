package fi.vincit.mutrproject.component.configuration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.User;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.jayway.restassured.RestAssured.given;

@Service
public class RestAssuredHelper {

    @Value("${local.server.port}")
    private int port;

    public void setUp() {
        RestAssured.port = port;
    }

    public void clear() {
        databaseUtil.clearDb();
    }

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    private UserService userService;

    private String username;
    private String password;
    private boolean isAnonymous;

    public RequestSpecification whenAuthenticated() {
        RequestSpecification spec = given();
        if (!isAnonymous) {
            spec = spec.auth().preemptive().basic(username, password);
        } else {
            spec = spec;
        }
        return spec.header("Content-Type", "application/json");
    }

    public void loginWithUser(User user) {
        username = user.getUsername();
        password = user.getUsername();
        isAnonymous = false;
    }

    public void loginAnonymous() {
        username = null;
        password = null;
        isAnonymous = true;
    }

}
