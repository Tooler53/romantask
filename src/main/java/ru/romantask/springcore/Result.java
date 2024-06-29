package ru.romantask.springcore;

import ru.romantask.springcore.models.Dish;
import ru.romantask.springcore.models.Ingredients;

import java.util.List;
import java.util.Map;

public class Result {
    private double totalCost;

    private Map<String, Integer> cookedDishes;

    private List<Ingredients> remainsIngredients;

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Map<String, Integer> getCookedDishes() {
        return cookedDishes;
    }

    public void setCookedDishes(Map<String, Integer> cookedDishes) {
        this.cookedDishes = cookedDishes;
    }

    public List<Ingredients> getRemainsIngredients() {
        return remainsIngredients;
    }

    public void setRemainsIngredients(List<Ingredients> remainsIngredients) {
        this.remainsIngredients = remainsIngredients;
    }
}
