package ru.romantask.springcore.helpers;

import ru.romantask.springcore.exceptions.RecipeParserException;

import java.io.*;
import java.nio.file.Path;

public class FileHelper {
    public static String getFileData(String path) throws IOException {
        String data;
        File file = Path.of(path).toFile();
        if (!file.exists() || !file.isFile()) {
            throw new RecipeParserException("Отсутствует файл");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            data = new String(fileInputStream.readAllBytes());
        }

        return data;
    }

    public static void saveResult (String path, byte[] resultBytes) throws IOException {
        File file = Path.of(path).toFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(resultBytes);
        }
    }
}
