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

        while (!result.getRemains().getRemains().stream().filter(ingredient -> ingredient.getCount() == 0).findAny().isPresent()) {
            for (Dish dish : recipe) {
                List<Boolean> allIngredients = new ArrayList<>();
                List<Ingredients> neededIngredients = new ArrayList<>();
                for (Ingredients ingredient : dish.getIngredients()) {
                    for (Ingredients remain : result.getRemains().getRemains()) {
                        if (ingredient.getName().equals(remain.getName()) && remain.getCount() >= ingredient.getCount()) {
                            allIngredients.add(true);
                            neededIngredients.add(new Ingredients(ingredient.getName(), ingredient.getCount()));
                        }
                    }
                }

                Optional<Boolean> allIngredientsSet = allIngredients.stream().filter(v -> false).findAny();

                if (allIngredientsSet.isEmpty()) {
                    for (Ingredients remain : result.getRemains().getRemains()) {
                        for (Ingredients neededIngredient : neededIngredients) {
                            if (remain.getName().equals(neededIngredient.getName())) {
                                remain.setCount(remain.getCount() - neededIngredient.getCount());
                            }
                        }
                    }

                    result.setFullCost(result.getFullCost() + dish.getPrice());
                    if (finishDishesList.contains(new FinishDishes(dish.getName(), 1))) {
                        int indexFinishDishesList = finishDishesList.indexOf(new FinishDishes(dish.getName()));
                        finishDishesList.set(indexFinishDishesList, new FinishDishes(dish.getName(), finishDishesList.get(indexFinishDishesList).getCount() + 1));
                    } else {
                        finishDishesList.add(new FinishDishes(dish.getName(), 1));
                    }
                }
            }

            result.setFinishDishes(finishDishesList);
        }
    }
}
