package pl.polsl.exceptions;

public class BeesHiveNotFound extends Exception {
    public BeesHiveNotFound(String message) {
        super(message);
    }

    public BeesHiveNotFound(String message, Throwable ErrorStackTrace) {
        super(message, ErrorStackTrace);
    }
}
