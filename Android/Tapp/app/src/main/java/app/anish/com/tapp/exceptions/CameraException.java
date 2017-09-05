package app.anish.com.tapp.exceptions;

/**
 * Created by anish_khattar25 on 9/1/17.
 */

public class CameraException extends Exception {

    public CameraException() {
        super();
    }

    public CameraException(String msg) {
        super(msg);
    }

    public CameraException(Throwable t) {
        super(t);
    }

    public CameraException(String msg, Throwable t) {
        super(msg, t);
    }


}
