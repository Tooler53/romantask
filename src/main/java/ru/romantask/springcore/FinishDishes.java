package ru.romantask.springcore;

import java.util.Objects;

public class FinishDishes {
    private String name;
    private int count;

    public FinishDishes() {
    }

    public FinishDishes(String name) {
        this.name = name;
    }

    public FinishDishes(String name, int count) {
        this(name);
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinishDishes that = (FinishDishes) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "FinishDishes{" +
               "name='" + name + '\'' +
               ", count=" + count +
               '}';
    }
}
