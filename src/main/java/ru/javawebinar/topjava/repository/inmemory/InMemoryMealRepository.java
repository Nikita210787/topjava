package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.USER_ID;

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
    public Collection<Meal> getAll(int userID) {
        Map<Integer,Meal> meals = userMealsMap.get(userID);
        return CollectionUtils.isEmpty(meals)? Collections.emptyList():
                meals.values().stream()
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }
}

