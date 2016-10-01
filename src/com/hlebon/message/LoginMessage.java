package com.hlebon.message;

import java.io.Serializable;

public class LoginMessage extends Message implements Serializable {
    private int parameter = 10;

    public int getParameter() {
        return parameter;
    }
}
