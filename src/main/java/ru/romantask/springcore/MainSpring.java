package ru.romantask.springcore;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.romantask.springcore.config.SpringConfig;
import ru.romantask.springcore.helpers.FileHelper;
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
//    private final static String RECIPE_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/recipe";
//    private final static String INGREDIENTS_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/ingredientsStorage";
//    private final static String RESULT_PATH = "/Users/konstantin/IdeaProjects/romantask2/src/main/resources/result";

    public static void main(String[] args) throws IOException {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            RecipeParser recipeParser = context.getBean("recipeParser", RecipeParser.class);
            IngredientsParser ingredientsParser = context.getBean("ingredientsStorageParser", IngredientsParser.class);
            Scanner input = new Scanner(System.in);

            System.out.print("Введите путь до файла \"рецепты\": ");
            String recipePath = input.nextLine();
            System.out.println();

            System.out.print("Введите путь до файла \"остатки ингредиентов\": ");
            String ingredientsPath = input.nextLine();
            System.out.println();

            System.out.print("Введите путь до файла \"результат\": ");
            String resultPath = input.nextLine();

            recipeParser.setPath(recipePath);
            ingredientsParser.setPath(ingredientsPath);

            List<Ingredients> remainIngredients = ingredientsParser.parse();
            List<Dish> dishes = recipeParser.parse();

            String resultString = getResultString(getResult(remainIngredients, dishes));

            //пишем результат в файл
            FileHelper.saveResult(resultPath, resultString.getBytes());
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static Result getResult(List<Ingredients> remainIngredients, List<Dish> dishes) {
        Result result = new Result();
        result.setCookedDishes(new HashMap<>());
        result.setRemainsIngredients(remainIngredients);

        while (!dishes.isEmpty()) {
            //находим самое прибыльное блюдо
            MaxCountDish maxCountDish = dishes.stream()
                    .map(dish -> maxDishes(dish, remainIngredients))
                    .reduce((dish1, dish2) -> dish1.getTotalCost() > dish2.getTotalCost() ? dish1 : dish2)
                    .get();
            //если количество > 0 добавляем его в список блюд и ссумируем количество
            if (maxCountDish.getCount() > 0) {
                result.setTotalCost(result.getTotalCost() + maxCountDish.getTotalCost());
                result.getCookedDishes().put(maxCountDish.getDish().getName(), maxCountDish.getCount());

                //вычитаем использованные ингридиенты
                maxCountDish.getDish().getIngredients().forEach(ingredient -> result.getRemainsIngredients().forEach(remainsIngredient -> {
                    if (remainsIngredient.getName().equals(ingredient.getName()) && remainsIngredient.getCount() - ingredient.getCount() >= 0) {
                        remainsIngredient.setCount(remainsIngredient.getCount() - ingredient.getCount());
                    }
                }));
            }

            dishes.remove(maxCountDish.getDish());
        }
        return result;
    }

    private static String getResultString(Result result) {
        List<Ingredients> remains = result.getRemainsIngredients();
        return "Общая стоимость блюд: " +
               result.getTotalCost() +
               "\n\nБлюда:\n" +
               result.getCookedDishes().entrySet().stream()
                       .map(dish -> dish.getKey() + ":" + dish.getValue())
                       .collect(Collectors.joining("\n")) +
               "\n\nОстатки:\n" +
               remains.stream()
                       .map(remain -> remain.getName() + ":" + remain.getCount())
                       .collect(Collectors.joining("\n"));
    }

    public static MaxCountDish maxDishes(Dish dish, List<Ingredients> remainIngredients) {
        List<Ingredients> dishIngredients = dish.getIngredients();
        List<Ingredients> ingredientsList = dishIngredients.stream()
                .filter(dishIngredient -> remainIngredients.stream()
                        .anyMatch(remainIngredient -> remainIngredient
                                .getName().equals(dishIngredient.getName())))
                .collect(Collectors.toList());

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

        //создаем новый объект с блюдом что бы не изменять из списка рецептов
        Dish newDish = new Dish(dish.getName(), dish.getPrice(), new ArrayList<>());

        //считаем инредиенты которые потратили на готовку
        dish.getIngredients().forEach(ingredient -> newDish.getIngredients().add(new Ingredients(ingredient.getName(), ingredient.getCount() * min)));

        return new MaxCountDish(min, dish.getPrice() * min, newDish);
    }
}
