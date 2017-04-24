package app.anish.com.tapp.exceptions;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class PhoneNumberNotFoundException extends Exception {

    public PhoneNumberNotFoundException() {
        super();
    }

    public PhoneNumberNotFoundException(String msg) {
        super(msg);
    }

    public PhoneNumberNotFoundException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
