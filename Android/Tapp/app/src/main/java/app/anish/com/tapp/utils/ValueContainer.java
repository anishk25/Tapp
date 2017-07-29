package app.anish.com.tapp.utils;

/**
 * A simple class to hold values of the Generic Type.
 * Created by anish_khattar25 on 7/25/17.
 */

public class ValueContainer<T> {

    private T val;

    public ValueContainer() {}

    public ValueContainer(T val) {
        this.val = val;
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }
}
