package ru.romantask.springcore;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.romantask.springcore.config.SpringConfig;

import java.io.IOException;

public class MainSpring {
    public static void main(String[] args) throws IOException {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            Kitchen kitchen = context.getBean("kitchen", Kitchen.class);
            kitchen.writeResult();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
