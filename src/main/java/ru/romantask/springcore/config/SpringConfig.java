package ru.romantask.springcore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.romantask.springcore.IngredientsStorage;
import ru.romantask.springcore.Kitchen;
import ru.romantask.springcore.Recipe;
import ru.romantask.springcore.parsers.IngredientsStorageParser;
import ru.romantask.springcore.parsers.RecipeParser;

@Configuration
public class SpringConfig {
    @Bean
    public IngredientsStorageParser ingredientsStorageParser() {
        return new IngredientsStorageParser();
    }

    @Bean
    public RecipeParser recipeParser() {
        return new RecipeParser();
    }

    @Bean
    public IngredientsStorage ingredientsStorage() {
        return new IngredientsStorage();
    }

    @Bean
    public Recipe recipe() {
        return new Recipe();
    }

    @Bean
    public Kitchen kitchen() {
        return new Kitchen(recipe(), ingredientsStorage());
    }
}
