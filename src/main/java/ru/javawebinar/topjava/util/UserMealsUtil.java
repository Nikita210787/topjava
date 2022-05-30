package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import javax.jws.soap.SOAPBinding;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        //  List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
      //  mealsTo.forEach(System.out::println);

         System.out.println(filteredByStreams(
         meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> filteredByCyclesUserMealWithExcess = new ArrayList<>();
        Map<LocalDate, Integer> dateMealsMap = new HashMap<>();

        for (UserMeal userMeal : meals)
            dateMealsMap.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), (x, y) -> x + y);

        for (UserMeal userMeal : meals) {
            boolean excess = false;
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                if (dateMealsMap.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay) excess = true;

                filteredByCyclesUserMealWithExcess.add(
                        new UserMealWithExcess(userMeal.getDateTime(),
                                userMeal.getDescription(),
                                userMeal.getCalories(),
                                excess));
            }
        }
        return filteredByCyclesUserMealWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

    /*    Map<LocalDate, Integer> map = meals.stream().collect(Collectors.groupingBy(UserMeal::getDate,Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream().filter(u -> TimeUtil.isBetweenHalfOpen(u.getDateTime().toLocalTime(), startTime, endTime))
                .map(u -> new UserMealWithExcess(u.getDateTime(), u.getDescription(), u.getCalories(), map.get(u.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());*/
        Map<LocalDate, Integer> dateMap = meals.stream()
                .collect(groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(
                        userMeal.getDateTime(),
                        userMeal.getDescription(),
                        userMeal.getCalories(),
                        dateMap.getOrDefault(userMeal.getDateTime().toLocalDate(), 0) > caloriesPerDay))
                .collect(toList());
    }
}
