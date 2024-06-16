package ru.romantask.springcore;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.helpers.FileHelper;
import ru.romantask.springcore.parsers.IngredientsStorageParser;
import ru.romantask.springcore.parsers.RecipeParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Kitchen {
    private static String recipePath;
    private static String ingredientsStoragePath;
    private static String resultPath;
    private Recipe recipe;
    private IngredientsStorage ingredientsStorage;

    public Kitchen() {
    }

    public Kitchen(Recipe recipe, IngredientsStorage ingredientsStorage) {
        this.recipe = recipe;
        this.ingredientsStorage = ingredientsStorage;
        Scanner input = new Scanner(System.in);

        // /Users/konstantin/IdeaProjects/RomanTask/src/main/resources/recipe
        System.out.print("Введите путь до файла \"рецепты\": ");
//        recipePath = input.nextLine();
        recipePath = "/Users/konstantin/IdeaProjects/RomanTask/src/main/resources/recipe";
        this.recipe.setParser(new RecipeParser(recipePath));

        // /Users/konstantin/IdeaProjects/RomanTask/src/main/resources/ingredientsStorage
        System.out.print("Введите путь до файла \"остатки ингредиентов\": ");
//        ingredientsStoragePath = input.nextLine();
        ingredientsStoragePath = "/Users/konstantin/IdeaProjects/RomanTask/src/main/resources/ingredientsStorage";
        this.ingredientsStorage.setParser(new IngredientsStorageParser(ingredientsStoragePath));

        // /Users/konstantin/IdeaProjects/RomanTask/src/main/resources/result
        System.out.print("Введите путь до файла \"результат\": ");
//        resultPath = input.nextLine();
        resultPath = "/Users/konstantin/IdeaProjects/RomanTask/src/main/resources/result";
        System.out.println();
    }

    public List<Dish> getRecipe() {
        return recipe.getDish();
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<Ingredients> getIngredientsStorage() {
        return ingredientsStorage.getIngredients();
    }

    public void setIngredientsStorage(IngredientsStorage ingredientsStorage) {
        this.ingredientsStorage = ingredientsStorage;
    }

    public void writeResult() throws IOException {
        Result calculateResult = getCalculateResult();

        String resultString = getResultString(calculateResult);

        FileHelper.saveResult(Kitchen.resultPath, resultString.getBytes());
    }

    private String getResultString(Result result) {
        List<Ingredients> remains = result.getRemains().getRemains();
        return "Общая стоимость блюд: " +
               result.getFullCost() +
               "\n\nБлюда:\n" +
               result.getFinishDishes().stream()
                       .map(dish -> dish.getName() + ":" + dish.getCount())
                       .collect(Collectors.joining("\n")) +
               "\n\nОстатки:\n" +
               remains.stream()
                       .map(remain -> remain.getName() + ":" + remain.getCount())
                       .collect(Collectors.joining("\n"));
    }

    private Result getCalculateResult() {
        Result result = new Result();
        List<Dish> recipe = getRecipe();
        List<FinishDishes> finishDishesList = new ArrayList<>();

        result.setRemains(new Remains(getIngredientsStorage()));
        List<Ingredients> remains = result.getRemains().getRemains();

        //проверяем что все блюда больше не могут быть приготовлены
        while (finishDishesList.stream().noneMatch(FinishDishes::isAllPrepared)) {
            for (Dish dish : recipe) {
                List<Ingredients> neededIngredients = new ArrayList<>();
                FinishDishes finishDishes;

                //Создаем объект с блюдом и количеством
                finishDishes = getFinishDishes(dish, finishDishesList);
                //проверяем можем ли мы приготовить блюдо исходя из остатков ингридиентов
                for (Ingredients ingredient : dish.getIngredients()) {
                    Ingredients remainIngredient = getIngredients(ingredient, remains);
                    if (remainIngredient != null && ingredient.getCount() <= remainIngredient.getCount()) {
                        neededIngredients.add(new Ingredients(ingredient.getName(), ingredient.getCount()));
                    } else {//если не можем пишем что это блюдо болше не может бытьб приготовлено
                        finishDishes.setAllPrepared(true);
                    }
                }

                if (!finishDishes.isAllPrepared()) {
                    for (Ingredients neededIngredient : neededIngredients) {
                        Ingredients remainIngredient = getIngredients(neededIngredient, remains);
                        if (remainIngredient != null && (remainIngredient.getCount() - neededIngredient.getCount()) >= 0) {
                            //списываем ингридинты
                            remainIngredient.setCount(remainIngredient.getCount() - neededIngredient.getCount());
                        } else {
                            finishDishes.setAllPrepared(true);
                        }
                    }
                }

                if (!finishDishes.isAllPrepared()) {//добавляем к количеству приготовленных блюд и добавляем к стоимости блюд
                    finishDishes.setCount(finishDishes.getCount() + 1);
                    result.setFullCost(result.getFullCost() + dish.getPrice());
                }
            }

            result.setFinishDishes(finishDishesList);
        }

        return result;
    }

    private static FinishDishes getFinishDishes(Dish dish, List<FinishDishes> finishDishesList) {
        FinishDishes finishDishes;
        if (finishDishesList.contains(new FinishDishes(dish.getName()))) {
            int indexFinishDishesList = finishDishesList.indexOf(new FinishDishes(dish.getName()));
            finishDishes = finishDishesList.get(indexFinishDishesList);
        } else {
            finishDishes = new FinishDishes(dish.getName());
            finishDishesList.add(finishDishes);
        }
        return finishDishes;
    }

    private static Ingredients getIngredients(Ingredients ingredient, List<Ingredients> remains) {
        return remains.stream()
                .filter(remain -> remain.getName()
                        .equals(ingredient.getName()))
                .findAny()
                .orElse(null);
    }
}
