package com.atoudeft.banque;

import com.atoudeft.banque.operations.OperationFacture;
import com.atoudeft.banque.operations.OperationRetrait;
import com.atoudeft.banque.operations.OperationTransfer;

public class CompteCheque extends CompteBancaire {
    private double balance = 0;

    /**
     * Crée un compte chèque.
     *
     * @param numero numéro du compte
     * @param type   type du compte
     */
    public CompteCheque(String numero, TypeCompte type) {
        super(numero, type);
    }


    public boolean crediter(double montant) {
        if (montant >= 0) {
            balance = balance+montant;
            historique.empiler(new OperationRetrait(TypeOperation.DEPOT, montant));
            return true;
        } else {
            return false;
        }
    }


    public boolean debiter(double montant) {
        if (montant >= 0) {
            if (balance >= montant) {
                balance = balance - montant;
                historique.empiler(new OperationRetrait(TypeOperation.RETRAIT, montant));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean payerFacture(String str1, double dou, String str2) {
        historique.empiler(new OperationFacture(TypeOperation.FACTURE, dou, str1, str2));
        return false;
    }

    public boolean transferer(double dou, String str) {
        historique.empiler(new OperationTransfer(TypeOperation.TRANSFER, dou, str));
        return false;
    }
}
