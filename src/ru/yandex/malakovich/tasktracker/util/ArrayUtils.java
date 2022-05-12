package ru.yandex.malakovich.tasktracker.util;

public class ArrayUtils {
    public static void trimElements(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
    }
}
