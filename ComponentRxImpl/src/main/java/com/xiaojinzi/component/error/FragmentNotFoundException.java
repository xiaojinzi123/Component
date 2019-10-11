package com.xiaojinzi.component.error;

/**
 * time   : 2019/01/09
 *
 * @author : xiaojinzi 30212
 */
public class FragmentNotFoundException extends RuntimeException  {
    public FragmentNotFoundException() {
    }

    public FragmentNotFoundException(String message) {
        super(message);
    }

    public FragmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FragmentNotFoundException(Throwable cause) {
        super(cause);
    }
}
