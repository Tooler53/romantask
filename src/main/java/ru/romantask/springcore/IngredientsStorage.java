package ru.romantask.springcore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.romantask.springcore.exceptions.IngredientsStorageParserException;
import ru.romantask.springcore.interfaces.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientsStorage {
    private List<Ingredients> ingredients = new ArrayList<>();
    private Parser parser;

    public IngredientsStorage() {
    }

    public IngredientsStorage(@Qualifier("ingredientsStorageParser") Parser parser) {
        this.parser = parser;
    }

    public List<Ingredients> getIngredients() {
        try {
            return parser.parse();
        } catch (IOException e) {
            throw new IngredientsStorageParserException(e);
        }
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }
}