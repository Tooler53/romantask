package ru.romantask.springcore.interfaces;

import ru.romantask.springcore.exceptions.RecipeParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface Parser<T> {

    public List<T> parse() throws RecipeParserException, IOException;

    public void setPath(String path);
    public String getPath();
}
