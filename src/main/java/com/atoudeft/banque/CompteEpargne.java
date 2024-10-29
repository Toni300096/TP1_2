package com.atoudeft.banque;

public class CompteEpargne extends CompteBancaire {
    private double balance = 0;
    private double limite = 1000;
    private double frais = 2;
    private double tauxInterets;

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

    public void ajouterInterets() {
        balance = balance + balance*tauxInterets;
    }


}
