package ru.javawebinar.topjava.service;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.JpaUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.*;
//Optional 2
//        •если сделан этот пункт, и кэш в тестах вообще отключен, то очищать уже ничего не нужно.
//        Проверь свою реализацию Optional 2:
//        убираем из AbstractUserServiceTest очистку кэшей
//        ставим над AbstractUserServiceTest @FixMethodOrder(MethodSorters.NAME_ASCENDING), чтобы зафиксировать порядок выполнения тестов
//        проверка отключения кэша Spring: меняем имена тестов на a1update, a2getAll. Запускаем JdbcUserServiceTest. Если кэш Spring отключен,
//        то все тесты пройдут успешно.
//        проверка отключения кэша 2го уровня: в тесте get вместо админа проверяем получение полььзователя с USER_ID
//        (т.е. такого же, которого меняем в тесте update), потом меняем имена тестов на a1update, a2get.
//        Запускает DataJpaUserServiceTest. Смотрим, успешно ли прошли все тесты.
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractUserServiceTest extends AbstractServiceTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractUserServiceTest.class);

    @Autowired
    protected UserService service;

    @Autowired
    private CacheManager cacheManager;

    @Lazy
    @Autowired
    protected JpaUtil jpaUtil;

    @Autowired
    private ApplicationContext appCtx;

    @Before
    public void setup() {
        if (!appCtx.containsBean("noOpCacheManager")) {
            Objects.requireNonNull(cacheManager.getCache("users")).clear();
        }
        if (!isJdbc()) {
            boolean isSecondLevelCache = jpaUtil.isHibernateSecondLevelCache();
            log.info("hibernate.cache.use_second_level_cache {} ", isSecondLevelCache);
            if (isSecondLevelCache) {
                jpaUtil.clear2ndLevelHibernateCache();
            }
        }
        log.info("Spring cache(-s) is in progress {} ", cacheManager.getCacheNames());
    }

    @Test
    public void create() {
        User created = service.create(getNew());
        int newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
        created = service.create(getNewGuest());
        newId = created.id();
        newUser = getNewGuest();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void duplicateMailCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new User(null, "Duplicate", "user@yandex.ru", "newPass", Role.USER)));
    }

    @Test
    public void delete() {
        service.delete(USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_ID));
    }

    @Test
    public void deleteAll() {
        service.delete(USER_ID);
        service.delete(ADMIN_ID);
        service.delete(GUEST_ID);
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, List.of());
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    public void get() {
        User user = service.get(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    public void getByEmail() {
        User user = service.getByEmail("admin@gmail.com");
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    public void getByNotExistEmail() {
        assertThrows(NotFoundException.class, () -> service.getByEmail("notexist@email.com"));
    }

    @Test
    public void update() {
        service.update(getUpdated());
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdated());
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, admin, guest, getUpdated());
    }

    @Test
    public void getAll() {
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, admin, guest, user);
    }

    @Test
    public void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "  ", "mail@yandex.ru", "password", Role.USER)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "  ", "password", Role.USER)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "mail@yandex.ru", "  ", Role.USER)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "mail@yandex.ru", "password", 9, true, new Date(), Set.of())));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "mail@yandex.ru", "password", 10001, true, new Date(), Set.of())));
    }
}