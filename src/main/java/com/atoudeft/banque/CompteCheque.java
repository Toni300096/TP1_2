package com.atoudeft.banque;

public class CompteCheque extends CompteBancaire {
    double balance = 0;

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
            return true;
        } else {
            return false;
        }
    }


    public boolean debiter(double montant) {
        if (montant >= 0) {
            if (balance >= montant) {
                balance = balance - montant;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean payerFacture(String str1, double dou, String str2) {
        return false;
    }

    public boolean transferer(double dou, String str) {
        return false;
    }
}
