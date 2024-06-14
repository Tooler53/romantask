package ru.romantask.springcore.parsers;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.Dish;
import ru.romantask.springcore.Ingredients;
import ru.romantask.springcore.exceptions.RecipeParserException;
import ru.romantask.springcore.interfaces.Parser;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        String data = getFileData();

        String[] dishesString = data.split("\n");
        List<Dish> dishes = new ArrayList<>();

        for (String dish : dishesString) {
            String[] dishElem = dish.split("\t");
            String[] ingredientsArray = dishElem[2].split(";");
            List<Ingredients> ingredientsList = new ArrayList<>();

            for (String inrg : ingredientsArray) {
                ingredientsList.add(new Ingredients(inrg.split(":")[0], Integer.parseInt(inrg.split(":")[1])));
            }

            dishes.add(new Dish(dishElem[0], Integer.parseInt(dishElem[1]), ingredientsList));
        }

        return dishes;
    }

    private String getFileData() throws IOException {
        String data;
        File file = Path.of(getPath()).toFile();
        if (!file.exists() || !file.isFile()) {
            throw new RecipeParserException("Отсутствует файл");
        }


        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            data = new String(fileInputStream.readAllBytes());
        }

        return data;
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
