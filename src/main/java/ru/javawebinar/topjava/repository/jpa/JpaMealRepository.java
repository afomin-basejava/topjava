package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.model.Meal.*;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = entityManager.getReference(User.class, userId);
        if (meal.isNew()) {
            meal.setUser(user);
            entityManager.persist(meal);
            return meal;
        } else if (get(meal.id(), userId) == null) {
            return null;
        }
        meal.setUser(user);
        return entityManager.merge(meal);
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return entityManager.createNamedQuery(DELETE)
                       .setParameter(1, id)
                       .setParameter(2, userId)
                       .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = entityManager.find(Meal.class, id);
        return meal != null && meal.getUser().getId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return entityManager.createNamedQuery(GET_ALL, Meal.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return entityManager.createNamedQuery(GET_BETWEEN, Meal.class)
                .setParameter("userId", userId)
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .getResultList();
    }
}