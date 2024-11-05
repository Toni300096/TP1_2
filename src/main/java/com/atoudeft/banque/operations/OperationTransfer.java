package com.atoudeft.banque.operations;

import com.atoudeft.banque.TypeOperation;

public class OperationTransfer extends Operation {
    private Double montant;
    private String numCompteDestinataire;

    public OperationTransfer(TypeOperation type, Double montant, String numCompteDestinataire) {
        super(type);
        this.montant = montant;
        this.numCompteDestinataire = numCompteDestinataire;
    }

    public String toString() {
        return "DATE:"+date+",TYPE:"+type+",MONTANT:"+montant;
    }
}
