package com.atoudeft.banque;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompteClient implements Serializable {
    private String numero;
    private String nip;
    private List<CompteBancaire> comptes;
    private double solde;

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
}
