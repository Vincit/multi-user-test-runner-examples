package fi.vincit.mutrproject.component.configuration;

import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.mutrproject.configuration.RoleGroup;
import fi.vincit.mutrproject.feature.user.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComponentTestConfig {

    @Bean(name = "testConfiguration")
    public MultiUserConfig<User, RoleGroup> testConfiguration() {
        return new TestConfiguration();
    }

    @Bean(name = "restTestConfiguration")
    public RestTestConfiguration restTestConfiguration() {
        return new RestTestConfiguration();
    }

    @Bean
    public RestAssuredHelper restAssuredHelper() {
        return new RestAssuredHelper();
    }

}
