package pl.polsl.exceptions;

import java.util.function.Supplier;

public class BeesHiveNotFound extends Exception {
    public BeesHiveNotFound(String message) {
        super(message);
    }

    public BeesHiveNotFound(String message, Throwable ErrorStackTrace) {
        super(message, ErrorStackTrace);
    }
}
