package com.atoudeft.banque;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompteClient implements Serializable {
    private String numero;
    private String nip;
    private List<CompteBancaire> comptes;
    private double solde;
    private double montantFacture;
    private String numeroFacture;


    /**
     * Crée un compte-client avec un numéro et un nip.
     *
     * @param numero le numéro du compte-client
     * @param nip le nip
     */
    public CompteClient(String numero, String nip) {
        this.numero = numero;
        this.nip = nip;
        comptes = new ArrayList<>();
    }

    /**
     * Ajoute un compte bancaire au compte-client.
     *
     * @param compte le compte bancaire
     * @return true si l'ajout est réussi
     */
    public boolean ajouter(CompteBancaire compte) {
        return this.comptes.add(compte);
    }

    public String getNumero() {
        return this.numero;
    }

    public List<CompteBancaire> getComptesBancaires() {
        return comptes;
    }
    public boolean verifierNip(String nip){
        return this.nip.equals(nip);
    }

    public void deposer(double montantDepot){
        if (montantDepot > 0){
            solde += montantDepot;
        }
        else {
            throw new IllegalArgumentException("le montant doit etre positif");
        }
    }
    public void retirer(double montantRetrait){
        if (montantRetrait > 0 && solde > 0){
            solde -= montantRetrait;
        }
        else {
            throw new IllegalArgumentException("le montant doit etre positif et le solde doit etre plus que 0");
        }
    }

    public boolean payerFacture(double montantFacture, String numeroFacture) {
        if (solde >= montantFacture) {
            solde -= montantFacture;

            return true;
        } else {
            return false;
        }
    }

    public boolean transferer(double montantTransfer, CompteClient compteDestinataire) {
        if (montantTransfer <= 0) {
            throw new IllegalArgumentException("Le montant du transfert doit être supérieur à zéro.");
        }

        if (solde >= montantTransfer) {
            solde -= montantTransfer;
            compteDestinataire.ajouterFonds(montantTransfer);
            return true;
        } else {
            return false;
        }
    }

    public void ajouterFonds(double montant) {
        solde += montant;
    }

    //(TRISTAN) Override de la fonction equals de Compte pour faire fonctionner l'indexOF de getCompteClient dans banque.
    public boolean equals(Object object) {
        if(object instanceof CompteClient){
            return this.numero.equals(((CompteClient) object).getNumero());
        }
        return this.equals(object);
    }
}
