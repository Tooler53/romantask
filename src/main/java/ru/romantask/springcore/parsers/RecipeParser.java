package ru.romantask.springcore.parsers;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.models.Dish;
import ru.romantask.springcore.exceptions.RecipeParserException;
import ru.romantask.springcore.helpers.FileHelper;
import ru.romantask.springcore.interfaces.Parser;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RecipeParser implements Parser<Dish> {
    String path;

    public RecipeParser() {
    }

    public RecipeParser(String path) {
        this.path = path;
    }

    @Override
    public List<Dish> parse() throws RecipeParserException, IOException {
        String data = FileHelper.getFileData(getPath());

        return Stream.of(data)
                .flatMap(pair -> Stream.of(pair.split("\n")))//разбиваем блюда через переос строки
                .map(dishesArray -> {
                    Dish dish = new Dish();
                    String[] element = dishesArray.split("\t");//разбиваем блюдо через табуляюцию и получаем название, стоимость и список ингридиентов

                    dish.setName(element[0]);
                    dish.setPrice(Integer.parseInt(element[1]));
                    dish.setIngredients(Stream.of(element[2])
                            .flatMap(ingredientArray -> Stream.of(ingredientArray.split(";")))//разбивает ингридинты через ;
                            .map(IngredientsParser::getIngredients)
                            .collect(Collectors.toList()));

                    return dish;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
