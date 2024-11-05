package com.atoudeft.banque;

import com.atoudeft.banque.operations.OperationFacture;
import com.atoudeft.banque.operations.OperationRetrait;
import com.atoudeft.banque.operations.OperationTransfer;

public class CompteEpargne extends CompteBancaire {
    private double balance = 0;
    private double limite = 1000;
    private double frais = 2;
    private double tauxInterets;
    private PileChainee historique;

    /**
     * Crée un compte épargne.
     *
     * @param numero numéro du compte
     * @param type   type du compte
     */
    public CompteEpargne(String numero, TypeCompte type, double tauxInterets) {
        super(numero, type);
        this.tauxInterets = tauxInterets;
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
                if (balance < limite) { // frais de 2$
                    balance = balance - frais;
                }
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

    public void ajouterInterets() {
        balance = balance + balance*tauxInterets;
    }


}
