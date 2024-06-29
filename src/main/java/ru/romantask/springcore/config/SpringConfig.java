package ru.romantask.springcore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.romantask.springcore.parsers.IngredientsParser;
import ru.romantask.springcore.parsers.RecipeParser;

@Configuration
public class SpringConfig {
    @Bean
    public IngredientsParser ingredientsStorageParser() {
        return new IngredientsParser();
    }

    @Bean
    public RecipeParser recipeParser() {
        return new RecipeParser();
    }
}
