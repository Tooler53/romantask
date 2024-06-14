package ru.romantask.springcore;

public class Ingredients {
    private String name;
    private int count;

    public Ingredients() {
    }

    public Ingredients(String name, int count) {
        this.name = name;
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
    public String toString() {
        return "{\n" +
               "\tname='" + name + '\'' +
               ",\n\tcount=" + count +
               "\n\t}";
    }
}
