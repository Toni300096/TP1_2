package com.atoudeft.banque.operations;

import com.atoudeft.banque.TypeOperation;

public class OperationRetrait extends Operation {
    private Double montant;

    public OperationRetrait(TypeOperation type, Double montant) {
        super(type);
        this.montant = montant;
    }

    public String toString() {
        return "DATE:"+date+",TYPE:"+type+",MONTANT:"+montant;
    }
}
