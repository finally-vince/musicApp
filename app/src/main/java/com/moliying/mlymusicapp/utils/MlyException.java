package com.moliying.mlymusicapp.utils;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/27.
 */
public class MlyException extends RuntimeException {
    public MlyException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
