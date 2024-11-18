package com.atoudeft.banque;

import com.atoudeft.banque.operations.OperationDepot;
import com.atoudeft.banque.operations.OperationFacture;
import com.atoudeft.banque.operations.OperationRetrait;
import com.atoudeft.banque.operations.OperationTransfer;

import java.io.Serializable;

public abstract class CompteBancaire implements Serializable {
    private String numero;
    private TypeCompte type;
    private double solde;
    final PileChainee historique;

    /**
     * Génère un numéro de compte bancaire aléatoirement avec le format CCC00C, où C est un caractère alphabétique
     * majuscule et 0 est un chiffre entre 0 et 9.
     * @return Un nouveau numéro pour le compte bancaire
     */
    public static String genereNouveauNumero() {
        char[] t = new char[6];
        for (int i=0;i<3;i++) {
            t[i] = (char)((int)(Math.random()*26)+'A');
        }
        for (int i=3;i<5;i++) {
            t[i] = (char)((int)(Math.random()*10)+'0');
        }
        t[5] = (char)((int)(Math.random()*26)+'A');
        return new String(t);
    }

    /**
     * Crée un compte bancaire.
     * @param numero numéro du compte
     * @param type type du compte
     */
    public CompteBancaire(String numero, TypeCompte type) {
        this.numero = numero;
        this.type = type;
        this.solde = 0;
        this.historique = new PileChainee();
    }
    public String getNumero() {
        return numero;
    }
    public TypeCompte getType() {
        return type;
    }
    public double getSolde() {return solde;}
    public PileChainee getHistorique() {
        return historique;
    }
    //(Nico + Tristan)
    public void deposer(double montantDepot){
        if (montantDepot > 0){
            solde += montantDepot;
            historique.empiler(new OperationDepot(TypeOperation.DEPOT,montantDepot));
        }
        else {
            throw new IllegalArgumentException("le montant doit etre positif");
        }
    }
    //(Nico + Tristan)
    public void retirer(double montantRetrait){
        if (montantRetrait > 0 && solde > 0){
            solde -= montantRetrait;
            historique.empiler(new OperationRetrait(TypeOperation.RETRAIT,montantRetrait));
        }
        else {
            throw new IllegalArgumentException("le montant doit etre positif et le solde doit etre plus que 0");
        }
    }
    //(Nico + Tristan)
    public boolean payerFacture(double montantFacture, String numeroFacture, String description) {
        if (solde >= montantFacture) {
            solde -= montantFacture;
            historique.empiler(new OperationFacture(TypeOperation.FACTURE,montantFacture,numeroFacture,description));
            return true;
        } else {
            return false;
        }

    }
    //(Nico + Tristan)
    public boolean transferer(double montantTransfer, CompteBancaire compteDestinataire) {
        if (montantTransfer <= 0) {
            throw new IllegalArgumentException("Le montant du transfert doit être supérieur à zéro.");
        }

        if (solde >= montantTransfer) {
            solde -= montantTransfer;
            compteDestinataire.ajouterFonds(montantTransfer);
            historique.empiler(new OperationTransfer(TypeOperation.FACTURE,montantTransfer,compteDestinataire.numero));
            return true;
        } else {
            return false;
        }
    }
    public void ajouterFonds(double montant) {
        solde += montant;
    }
}