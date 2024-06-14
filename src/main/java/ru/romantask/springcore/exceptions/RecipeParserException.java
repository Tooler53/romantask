package ru.romantask.springcore.exceptions;

public class RecipeParserException extends RuntimeException {
    private String message;

    public RecipeParserException(String message) {
        super(message);
    }

    public RecipeParserException(Throwable throwable) {
        super(throwable);
    }
}
