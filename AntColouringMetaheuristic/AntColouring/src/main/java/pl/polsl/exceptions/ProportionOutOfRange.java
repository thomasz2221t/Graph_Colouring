package pl.polsl.exceptions;

public class ProportionOutOfRange extends Exception{
    public ProportionOutOfRange(String message) {
        super(message);
    }
    public ProportionOutOfRange(String message, Throwable ErrorStackTrace) {
        super(message, ErrorStackTrace);
    }
}
