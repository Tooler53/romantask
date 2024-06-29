package ru.romantask.springcore.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private String name;
    private int price;

    List<Ingredients> ingredients = new ArrayList<>();

    public Dish() {
    }

    public Dish(String name, int price, List<Ingredients> ingredients) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Dish {\n" +
               "name='" + name + '\'' +
               ",\nprice=" + price +
               ",\ningredients=" + ingredients +
               "\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(name, dish.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
