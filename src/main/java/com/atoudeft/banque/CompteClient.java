package com.atoudeft.banque;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompteClient implements Serializable {
    private String numero;
    private String nip;
    private List<CompteBancaire> comptes;
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
    //Permet de trouver un compte par son numéro(string) devrait etre favoriser par rapport a getComptesBancaires. (Tristan)
    public CompteBancaire getCompteBancaire(String numero) {
        for (CompteBancaire compte : comptes) {
            if (compte.getNumero().equals(numero)) {
                return compte;
            }
        }
        return null;
    }
    public boolean verifierNip(String nip){
        return this.nip.equals(nip);
    }



    //(TRISTAN) Override de la fonction equals de Compte pour faire fonctionner l'indexOF de getCompteClient dans banque.
    public boolean equals(Object object) {
        if(object instanceof CompteClient){
            return this.numero.equals(((CompteClient) object).getNumero());
        }
        return this.equals(object);
    }
}
