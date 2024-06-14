package ru.romantask.springcore.parsers;

import org.springframework.stereotype.Component;
import ru.romantask.springcore.Ingredients;
import ru.romantask.springcore.exceptions.RecipeParserException;
import ru.romantask.springcore.interfaces.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        String data = getFileData();

        String[] supplies = data.split("\n");
        List<Ingredients> ingredientsList = new ArrayList<>();

        for (String supply : supplies) {
            String[] splittedSupply = supply.split(":");
            ingredientsList.add(new Ingredients(splittedSupply[0], Integer.parseInt(splittedSupply[1])));
        }

        return ingredientsList;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    private String getFileData() throws IOException {
        String data = null;

        File file = Path.of(getPath()).toFile();
        if (!file.exists() || !file.isFile() || file.length() == 0 || !file.canRead()) {
            throw new RecipeParserException("Отсутствует файл");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            data = new String(fileInputStream.readAllBytes());
        }

        return data;
    }
}
