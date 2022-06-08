package ru.yandex.malakovich.tasktracker.exception;

import ru.yandex.malakovich.tasktracker.model.Task;

public class InvalidStartTimeException extends RuntimeException {
    public InvalidStartTimeException(String message) { super(message); }

    public InvalidStartTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStartTimeException(Task existing, Task attempted) {
        super("New task " + attempted + " overlaps existing " + existing);
    }
}
