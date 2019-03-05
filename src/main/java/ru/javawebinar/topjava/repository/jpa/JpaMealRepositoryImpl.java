package ru.javawebinar.topjava.repository.jpa;

import org.hibernate.annotations.Proxy;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepositoryImpl implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User ref = em.getReference(User.class, userId);
        meal.setUser(ref);
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        } else {
            return em.merge(meal);
        }

    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        User ref = em.getReference(User.class, userId);
        return em.createNamedQuery(Meal.DELETE)
                .setParameter("id", id)
                .setParameter("user", ref)
                .executeUpdate() != 0;
    }

    @Override
    @Transactional
    public Meal get(int id, int userId) {
        User ref = em.getReference(User.class, userId);
        try {
            Meal meal = em.createNamedQuery(Meal.GET, Meal.class)
                    .setParameter("id", id)
                    .setParameter("user", ref)
                    .getSingleResult();
//            meal.setUser(null);
            return meal;
        } catch (NoResultException e) {
            throw new NotFoundException("meal not found");
        }

//        Meal meal = em.find(Meal.class, id);
//        if (meal.getUser().equals(ref)) {
//            return meal;
//        } else {
//            return null;
//        }
    }

    @Override
    @Transactional
    public List<Meal> getAll(int userId) {
        User ref = em.getReference(User.class, userId);
        return em.createNamedQuery(Meal.GET_ALL, Meal.class)
                .setParameter("user", ref)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        User ref = em.getReference(User.class, userId);
        List<Meal> meals = em.createNamedQuery(Meal.GET_BETWEEN, Meal.class)
                .setParameter("user", ref)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        return meals;
    }
}