package com.hlebon.message;

import java.io.Serializable;

public class SayMessage extends Message implements Serializable {
    private static final long serialVersionUID = -1522117679723983086L;

    private Character c = '\uACED';
    private Character m = '\uACED';

    public int getParameter() {
        return 10;
    }

    public Character getC() {
        return c;
    }
}
