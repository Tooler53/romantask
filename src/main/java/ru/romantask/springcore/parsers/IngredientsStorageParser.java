package ru.romantask.springcore.parsers;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.Ingredients;
import ru.romantask.springcore.helpers.FileHelper;
import ru.romantask.springcore.interfaces.Parser;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class IngredientsStorageParser implements Parser<Ingredients> {
    String path;

    public IngredientsStorageParser() {
    }

    public IngredientsStorageParser(String path) {
        this.path = path;
    }

    @Override
    public List<Ingredients> parse() throws IOException {
        String data = FileHelper.getFileData(getPath());

        return Stream.of(data)
                .flatMap(ingredientsArray -> Stream.of(ingredientsArray.split("\n")))
                .map(IngredientsStorageParser::getIngredients)
                .collect(Collectors.toList());
    }

    public static Ingredients getIngredients(String ingredientsArray) {
        Ingredients ingredients = new Ingredients();
        String[] ingredient = ingredientsArray.split(":");
        ingredients.setName(ingredient[0]);
        ingredients.setCount(Integer.parseInt(ingredient[1]));
        return ingredients;
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
