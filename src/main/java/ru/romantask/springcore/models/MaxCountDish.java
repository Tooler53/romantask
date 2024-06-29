package ru.romantask.springcore.models;

import java.util.List;

public class MaxCountDish {
    private int count;

    private int totalCost;

    private Dish dish;

    public MaxCountDish() {
    }

    public MaxCountDish(int count, int totalCost, Dish dish) {
        this.count = count;
        this.totalCost = totalCost;
        this.dish = dish;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }
}
