package ru.javawebinar.topjava.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.to.UserTo;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUIController extends AbstractUserController {
    @Autowired
    MessageSource messageSource;

    @Override
    @GetMapping
    public List<User> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createOrUpdate(@Valid UserTo userTo) {
        try {
            if (userTo.isNew()) {
                super.create(userTo);
            } else {
                super.update(userTo, userTo.id());
            }
        } catch (DataIntegrityViolationException dive) {
            throw new IllegalArgumentException(Objects.requireNonNull(messageSource.getMessage("exception.user.email.duplicated",
                    null,
                    "DataIntegrityViolationException",
                    LocaleContextHolder.getLocale())));
        }
    }

    @Override
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        super.enable(id, enabled);
    }
}
