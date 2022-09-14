package ru.javawebinar.topjava.web.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.springframework.core.env.Profiles.of;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.UserTestData.*;

public class AbstractUserControllerTest extends AbstractControllerTest {

    @Autowired
    protected UserService userService;

    protected void assumeDatajpaProfile() {
        Environment environment = webApplicationContext.getEnvironment();
        Assumptions.assumeTrue(environment.acceptsProfiles(of(Profiles.DATAJPA)),
                format("Active profile -- %s-- is not DATAJPA", stream(environment.getActiveProfiles()).reduce("", (p1, p2) -> p1 + p2 + " ")));
    }

     protected void getWihtMeals(String url, User user) throws Exception {
        assumeDatajpaProfile();
        perform(MockMvcRequestBuilders.get(url))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        USER_WITH_MEAL_MATCHER.contentJson(user))
                .andDo(print());
    }

    protected void getWihtMealsUsingAssertions(String url, User user) throws Exception {
        assumeDatajpaProfile();
        MvcResult mvcResult = perform(MockMvcRequestBuilders.get(url))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        Assertions.assertThat(JsonUtil.readValue(mvcResult.getResponse().getContentAsString(), User.class))
                .usingRecursiveComparison()
                .ignoringFields("meals.user", "registered")
                .isEqualTo(user);
    }

    void getWihtMealsUsingMatcher(String url, User user) throws Exception {
        assumeDatajpaProfile();
        MvcResult mvcResult = perform(MockMvcRequestBuilders.get(url))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        USER_WITH_MEAL_MATCHER.assertMatch(JsonUtil.readValue(mvcResult.getResponse().getContentAsString(), User.class), user);
    }
}
