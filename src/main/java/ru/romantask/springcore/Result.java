package ru.romantask.springcore;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private int fullCost;
    private List<FinishDishes> finishDishes = new ArrayList<>();

    private Remains remains;

    public Result() {
    }

    public Result(int fullCost, List<FinishDishes> finishDishes, Remains remains) {
        this.fullCost = fullCost;
        this.finishDishes = finishDishes;
        this.remains = remains;
    }

    public int getFullCost() {
        return fullCost;
    }

    public void setFullCost(int fullCost) {
        this.fullCost = fullCost;
    }

    public List<FinishDishes> getFinishDishes() {
        return finishDishes;
    }

    public void setFinishDishes(List<FinishDishes> finishDishes) {
        this.finishDishes = finishDishes;
    }

    public Remains getRemains() {
        return remains;
    }

    public void setRemains(Remains remains) {
        this.remains = remains;
    }

    @Override
    public String toString() {
        return "Result{" +
               "fullCost=" + fullCost +
               ", finishDishes=" + finishDishes +
               ", remains=" + remains +
               '}';
    }
}
