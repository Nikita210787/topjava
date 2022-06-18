package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.USER_ID;
@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> userMealsMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, USER_ID));
        save(new Meal(LocalDateTime.of(2015, Month.JUNE, 1, 14, 0), "Ланч Админ", 510), SecurityUtil.authUserId());
        save(new Meal(LocalDateTime.of(2015, Month.JUNE, 1, 21, 0), "Ужин Админ", 1510),SecurityUtil.authUserId());
    }

    @Override
    public Meal save(Meal meal, int userID) {
        Map<Integer, Meal> meals = userMealsMap.computeIfAbsent(userID, ConcurrentHashMap::new);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            return meal;
        }

        return meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userID) {
        Map<Integer, Meal> meals = userMealsMap.get(userID);
        return meals != null && meals.remove(id) != null;
    }

    @Override
    public Meal get(int id,int userID) {
        Map<Integer,Meal> meals = userMealsMap.get(userID);
        return meals==null? null:meals.get(id);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDAteTime, LocalDateTime endDateTime, int userId) {
        return getAllFiltred(userId,meal -> Util.isBetweenHalfOpen(meal.getDateTime(),startDAteTime,endDateTime));
    }

    @Override
    public List<Meal> getAll(int userId){
        return getAllFiltred(userId,meal->true);
    }
    public List<Meal> getAllFiltred(int userID,Predicate<Meal> filter) {

        Map<Integer,Meal> meals = userMealsMap.get(userID);
        return CollectionUtils.isEmpty(meals)? Collections.emptyList():
                meals.values().stream()
                        .filter(filter)
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }
}

