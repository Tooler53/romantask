package ru.romantask.springcore;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.romantask.springcore.config.SpringConfig;
import ru.romantask.springcore.models.Dish;
import ru.romantask.springcore.models.Ingredients;
import ru.romantask.springcore.models.MaxCountDish;
import ru.romantask.springcore.parsers.IngredientsParser;
import ru.romantask.springcore.parsers.RecipeParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainSpring {
    private final static String RECIPE_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/recipe";
    private final static String INGREDIENTS_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/ingredientsStorage";
    private final static String RESULT_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/result";

    public static void main(String[] args) throws IOException {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            RecipeParser recipeParser = context.getBean("recipeParser", RecipeParser.class);
            recipeParser.setPath(RECIPE_PATH);
            List<Dish> dishes = recipeParser.parse();

            IngredientsParser ingredientsParser = context.getBean("ingredientsStorageParser", IngredientsParser.class);
            ingredientsParser.setPath(INGREDIENTS_PATH);
            List<Ingredients> remainIngredients = ingredientsParser.parse();

            Result result = new Result();
            result.setCookedDishes(new HashMap<>());
            result.setRemainsIngredients(remainIngredients);

            while (!dishes.isEmpty()) {
                //находим самое прибыльное блюдо
                MaxCountDish maxCountDish = dishes.stream()
                        .map(dish -> maxDishes(dish, remainIngredients))
                        .reduce((dish1, dish2) -> dish1.getTotalCost() > dish2.getTotalCost() ? dish1 : dish2)
                        .get();

                if (maxCountDish.getCount() > 0) {
                    result.setTotalCost(result.getTotalCost() + maxCountDish.getTotalCost());
                    result.getCookedDishes().put(maxCountDish.getDish().getName(), maxCountDish.getCount());

                    for (Ingredients ingredient : maxCountDish.getDish().getIngredients()) {
                        result.getRemainsIngredients().forEach(remainsIngredient -> {
                            if (remainsIngredient.getName().equals(ingredient.getName()) && remainsIngredient.getCount() - ingredient.getCount() >= 0) {
                                remainsIngredient.setCount(remainsIngredient.getCount() - ingredient.getCount());
                            }
                        });
                    }
                }


                dishes.remove(maxCountDish.getDish());
            }

            System.out.println();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static MaxCountDish maxDishes(Dish dish, List<Ingredients> remainIngredients) {
        List<Ingredients> dishIngredients = dish.getIngredients();
        List<Ingredients> ingredientsList = dishIngredients.stream().filter(dishIngredient -> remainIngredients.stream().anyMatch(remainIngredient -> remainIngredient.getName().equals(dishIngredient.getName()))).collect(Collectors.toList());

        //если количество ингредиентов для блюда больше чем есть на складе то мы не можем приготовить такое блюдо
        if (dishIngredients.size() > ingredientsList.size()) {
            return new MaxCountDish(0, 0, null);
        }

        //подсчитываем максимально кол-во ингредиентов для блюда этого типа
        List<Ingredients> neededIngredients = dishIngredients.stream()
                .map(dishIngredient -> {
                    Ingredients ingredient = new Ingredients();
                    ingredient.setName(dishIngredient.getName());
                    int countIngredients = (remainIngredients.stream()
                                                    .filter(remainIngredient -> remainIngredient.getName().equals(dishIngredient.getName()))
                                                    .findAny().get()
                                                    .getCount() / dishIngredient.getCount());
                    ingredient.setCount(countIngredients);
                    return ingredient;
                }).collect(Collectors.toList());

        int min = neededIngredients.stream().mapToInt(Ingredients::getCount).min().getAsInt();

        //если кол-во индредиентов < 0 то мы не можем приготовить блюдо
        if (min < 0) {
            return new MaxCountDish(0, 0, null);
        }

        Dish newDish = new Dish(dish.getName(), dish.getPrice(), new ArrayList<>());

        //считаем инредиенты которые потратили на готовку
        for (Ingredients ingredient : dish.getIngredients()) {
            newDish.getIngredients().add(new Ingredients(ingredient.getName(), ingredient.getCount() * min));
        }

        return new MaxCountDish(min, dish.getPrice() * min, newDish);
    }
}
