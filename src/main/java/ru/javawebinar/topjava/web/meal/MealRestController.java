package ru.javawebinar.topjava.web.meal;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

public class MealRestController {
    private MealService service;
    //TODO
    public Meal create(Meal meal) {return service.create(meal);}

    public void delete(int id) {service.delete(id);}

    public Meal get(int id) {return service.get(id);}

    public List<Meal> getAll() {
        return (List<Meal>) service.getAll();
    }

    public void update(Meal meal) {service.create(meal);}

}