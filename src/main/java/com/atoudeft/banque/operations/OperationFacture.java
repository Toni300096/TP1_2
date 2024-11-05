package com.atoudeft.banque.operations;

import com.atoudeft.banque.TypeOperation;

public class OperationFacture extends Operation {
    private Double montant;
    private String numFacture;
    private String description;

    public OperationFacture(TypeOperation type, Double montant, String numFacture, String description) {
        super(type);
        this.montant = montant;
        this.numFacture = numFacture;
        this.description = description;
    }

    public String toString() {
        return "DATE:"+date+",TYPE:"+type+",MONTANT:"+montant;
    }
}
