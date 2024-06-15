package ru.romantask.springcore;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.parsers.IngredientsStorageParser;
import ru.romantask.springcore.parsers.RecipeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

@Component
public class Kitchen {
    private static String recipePath;
    private static String ingredientsStoragePath;
    private static String resultPath;
    private Recipe recipe;
    private IngredientsStorage ingredientsStorage;

    private Result result = new Result();

    public Kitchen() {
    }

    public Kitchen(Recipe recipe, IngredientsStorage ingredientsStorage) {
        this.recipe = recipe;
        this.ingredientsStorage = ingredientsStorage;
        Scanner input = new Scanner(System.in);

        // /Users/konstantin/IdeaProjects/RomanTask/src/main/resources/recipe
        System.out.print("Введите путь до файла \"рецепты\": ");
        recipePath = input.nextLine();
        this.recipe.setParser(new RecipeParser(recipePath));

        // /Users/konstantin/IdeaProjects/RomanTask/src/main/resources/ingredientsStorage
        System.out.print("Введите путь до файла \"остатки ингредиентов\": ");
        ingredientsStoragePath = input.nextLine();
        this.ingredientsStorage.setParser(new IngredientsStorageParser(ingredientsStoragePath));

        // /Users/konstantin/IdeaProjects/RomanTask/src/main/resources/result
        System.out.print("Введите путь до файла \"результат\": ");
        resultPath = input.nextLine();
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void writeResult() {
        List<Dish> recipe = getRecipe();
        List<FinishDishes> finishDishesList = new ArrayList<>();

        result.setRemains(new Remains(getIngredientsStorage()));

        while (finishDishesList.stream().noneMatch(FinishDishes::isAllPrepared)) {
            for (Dish dish : recipe) {
                List<Ingredients> neededIngredients = new ArrayList<>();
                FinishDishes finishDishes;

                if (finishDishesList.contains(new FinishDishes(dish.getName()))) {
                    int indexFinishDishesList = finishDishesList.indexOf(new FinishDishes(dish.getName()));
                    finishDishes = finishDishesList.get(indexFinishDishesList);
                } else {
                    finishDishes = new FinishDishes(dish.getName());
                    finishDishesList.add(finishDishes);
                }

                for (Ingredients ingredient : dish.getIngredients()) {
                    Ingredients remainIngredient = result.getRemains().getRemains().stream().filter(remain -> remain.getName().equals(ingredient.getName())).findAny().orElse(null);
                    if (remainIngredient != null && ingredient.getCount() <= remainIngredient.getCount()) {
                        neededIngredients.add(new Ingredients(ingredient.getName(), ingredient.getCount()));
                    } else {
                        finishDishes.setAllPrepared(true);
                    }
                }

                if (!finishDishes.isAllPrepared()) {
                    for (Ingredients neededIngredient : neededIngredients) {
                        Ingredients remainIngredient = result.getRemains().getRemains().stream().filter(remain -> remain.getName().equals(neededIngredient.getName())).findAny().orElse(null);
                        if (remainIngredient != null && remainIngredient.getCount() - neededIngredient.getCount() >= 0) {
                            remainIngredient.setCount(remainIngredient.getCount() - neededIngredient.getCount());
                        } else {
                            finishDishes.setAllPrepared(true);
                        }
                    }
                }

                if (!finishDishes.isAllPrepared()) {
                    finishDishes.setCount(finishDishes.getCount() + 1);
                    result.setFullCost(result.getFullCost() + dish.getPrice());
                }
            }

            result.setFinishDishes(finishDishesList);

            //todo сделать запись в файл
        }
    }
}
