package ru.romantask.springcore;

import java.util.ArrayList;
import java.util.List;

public class Remains {
    private List<Ingredients> remains = new ArrayList<>();

    public Remains() {
    }

    public Remains(List<Ingredients> remains) {
        this.remains = remains;
    }

    public List<Ingredients> getRemains() {
        return remains;
    }

    public void setRemains(List<Ingredients> remains) {
        this.remains = remains;
    }

    @Override
    public String toString() {
        return "Remains{" +
               "remains=" + remains +
               '}';
    }
}
