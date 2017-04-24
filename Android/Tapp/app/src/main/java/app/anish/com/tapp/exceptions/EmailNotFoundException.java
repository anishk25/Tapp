package app.anish.com.tapp.exceptions;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class EmailNotFoundException extends Exception {

    public EmailNotFoundException() {
        super();
    }

    public EmailNotFoundException(String msg) {
        super(msg);
    }

    public EmailNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
