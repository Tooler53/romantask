package ru.romantask.springcore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.romantask.springcore.exceptions.RecipeParserException;
import ru.romantask.springcore.interfaces.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Recipe {
    private List<Dish> dish = new ArrayList<>();

    private Parser parser;

    public Recipe() {
    }

    public Recipe(@Qualifier("recipeParser") Parser parser) {
        this.parser = parser;
    }

    public List<Dish> getDish() {
        try {
            return parser.parse();
        } catch (IOException e) {
            throw new RecipeParserException(e);
        }
    }

    public void setDish(List<Dish> dish) {
        this.dish = dish;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }
}
