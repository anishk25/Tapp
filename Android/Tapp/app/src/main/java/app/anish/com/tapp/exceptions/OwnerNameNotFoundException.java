package app.anish.com.tapp.exceptions;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class OwnerNameNotFoundException extends Exception {

    public OwnerNameNotFoundException() {
        super();
    }

    public OwnerNameNotFoundException(String msg) {
        super(msg);
    }

    public OwnerNameNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
