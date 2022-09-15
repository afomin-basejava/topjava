package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.springframework.core.env.Profiles.of;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.UserTestData.USER_WITH_MEALS_MATCHER;

public abstract class AbstractUserControllerTest extends AbstractControllerTest {

    @Autowired
    protected UserService userService;

    protected void assumeDatajpaProfile() {
        Environment environment = webApplicationContext.getEnvironment();
        Assumptions.assumeTrue(environment.acceptsProfiles(of(Profiles.DATAJPA)),
                format("Active profile -- %s-- is not DATAJPA", stream(environment.getActiveProfiles()).reduce("", (p1, p2) -> p1 + p2 + " ")));
    }

     protected void getWithMeals(String url, User user) throws Exception {
        assumeDatajpaProfile();
        perform(MockMvcRequestBuilders.get(url))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        USER_WITH_MEALS_MATCHER.contentJson(user))
                .andDo(print());
    }
}
