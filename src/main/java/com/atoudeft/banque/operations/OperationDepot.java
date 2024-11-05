package com.atoudeft.banque.operations;

import com.atoudeft.banque.TypeOperation;

public class OperationDepot extends Operation {
    private Double montant;

    public OperationDepot(TypeOperation type, Double montant) {
        super(type);
        this.montant = montant;
    }

    public String toString() {
        return "DATE:"+date+",TYPE:"+type+",MONTANT:"+montant;
    }
}
