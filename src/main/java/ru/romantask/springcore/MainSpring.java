package ru.romantask.springcore;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.romantask.springcore.config.SpringConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MainSpring {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            Kitchen kitchen = context.getBean("kitchen", Kitchen.class);
            kitchen.writeResult();

            System.out.println(kitchen.getResult());
        } catch (Exception ex) {
            throw ex;
        }
    }
}
