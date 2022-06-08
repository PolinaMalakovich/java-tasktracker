package ru.yandex.malakovich.tasktracker.util;

import java.time.LocalDateTime;

public class DateTimeUtils {
    public static LocalDateTime minDate(LocalDateTime left, LocalDateTime right) {
        if (left == null) return right;
        if (right == null) return left;
        return left.isBefore(right) ? left : right;
    }

    public static LocalDateTime maxDate(LocalDateTime left, LocalDateTime right) {
        if (left == null) return right;
        if (right == null) return left;
        return left.isBefore(right) ? right : left;
    }
}
