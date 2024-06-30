package ru.romantask.springcore;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.helpers.FileHelper;
import ru.romantask.springcore.parsers.IngredientsStorageParser;
import ru.romantask.springcore.parsers.RecipeParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Kitchen {
    private final static String RECIPE_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/recipe";
    private final static String INGREDIENTS_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/ingredientsStorage";
    private final static String RESULT_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/result";

    private String resultPath;

    private Recipe recipe;
    private IngredientsStorage ingredientsStorage;

    public Kitchen() {
    }

    public Kitchen(Recipe recipe, IngredientsStorage ingredientsStorage) {
        this.recipe = recipe;
        this.ingredientsStorage = ingredientsStorage;
        Scanner input = new Scanner(System.in);

        System.out.print("Введите путь до файла \"рецепты\": ");
        String recipePath = input.nextLine();
//        this.recipe.setParser(new RecipeParser(RECIPE_PATH));

        System.out.print("Введите путь до файла \"остатки ингредиентов\": ");
        String ingredientsStoragePath = input.nextLine();
//        this.ingredientsStorage.setParser(new IngredientsStorageParser(INGREDIENTS_PATH));

        System.out.print("Введите путь до файла \"результат\": ");
        resultPath = input.nextLine();
//        resultPath = RESULT_PATH;
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
        Result calculateResult = getCalculateResult2();

        String resultString = getResultString(calculateResult);

        FileHelper.saveResult(resultPath, resultString.getBytes());
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

    private Result getCalculateResult2() {
        Result result = new Result();
        List<Dish> recipe = getRecipe();
        result.setRemains(new Remains(getIngredientsStorage()));
        List<Ingredients> ingredients = result.getRemains().getRemains();

        for (int i = 0; i < recipe.size(); ) {
            Dish dish = recipe.get(i);
            List<Ingredients> dishIngredients = dish.getIngredients();

            List<Ingredients> ingredientsList = dishIngredients.stream()
                    .filter(dishIngredient -> ingredients.stream()
                            .anyMatch(remainIngredient -> remainIngredient
                                    .getName().equals(dishIngredient.getName())))
                    .collect(Collectors.toList());

            List<Ingredients> neededIngredients = dishIngredients.stream()
                    .map(dishIngredient -> {
                        Ingredients ingredient = new Ingredients();
                        ingredient.setName(dishIngredient.getName());
                        int countIngredients = (ingredients.stream()
                                                        .filter(remainIngredient -> remainIngredient.getName().equals(dishIngredient.getName()))
                                                        .findAny().get()
                                                        .getCount() / dishIngredient.getCount());
                        ingredient.setCount(countIngredients);
                        return ingredient;
                    }).collect(Collectors.toList());

            int min = neededIngredients.stream().mapToInt(Ingredients::getCount).min().getAsInt();

            if (dishIngredients.size() <= ingredientsList.size() && min > 0) {
                Dish newDish = new Dish(dish.getName(), dish.getPrice(), new ArrayList<>());
                dish.getIngredients().forEach(ingredient -> newDish.getIngredients().add(new Ingredients(ingredient.getName(), ingredient.getCount() * min)));

                result.getFinishDishes().add(new FinishDishes(newDish.getName(), min));
                result.setFullCost(result.getFullCost() + (newDish.getPrice() * min));

                result.getRemains().getRemains().forEach(remainIngredient -> {
                    Optional<Ingredients> ingredient = newDish.getIngredients().stream()
                            .filter(dishIngredient -> dishIngredient.getName().equals(remainIngredient.getName()))
                            .findAny();

                    if (ingredient.isPresent() && remainIngredient.getCount() - ingredient.get().getCount() >= 0) {
                        remainIngredient.setCount(remainIngredient.getCount() - ingredient.get().getCount());
                    }
                });
            }

            i++;
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
