package com.atoudeft.banque.operations;

import com.atoudeft.banque.TypeOperation;
import java.io.Serializable;

public abstract class Operation implements Serializable{
    final TypeOperation type;
    long date;

    public Operation(TypeOperation type) { // constructeur
        this.type = type;
        this.date = System.currentTimeMillis();
    }
}
