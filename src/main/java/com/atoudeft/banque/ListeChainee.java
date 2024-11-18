package com.atoudeft.banque;

import java.io.Serializable;

/**
 * Cette classe implémente une liste chainée
 *
 * Le type de l'élément est abstrait en utilisant la classe Objet
 *
 * Cette classe dépend de la classe Noeud
 */
public class ListeChainee implements Serializable {
    private Noeud tete = null;
    private int nbElement = 0;

    // Constructeur par défaut
    public ListeChainee() {

    }

    // accesseurs
    public boolean estVide()    { return nbElement == 0; }
    public int getTaille()      { return nbElement; }

    /**
     * Ajoute un élément à la tête de la liste.
     * @param obj l'élément à ajouter.
     */
    public void ajouterDebut(Object obj ) {
        // encapsuler l'objet dans un nouveau noeud
        Noeud nouveau = new Noeud(obj);

        // gérer le cas où la liste est vide
        if (tete == null) {
            tete = nouveau;
        } else {
            // tous les autres cas
            nouveau.setSuivant(tete);
            tete = nouveau;
        }
        // mettre à jour la taille
        nbElement++;
    }

    /**
     * Retirer un élément à la fin de la liste et le retourner.
     * @return L'objet retiré, null si la liste est vide.
     */
    public Object retirerFin() {
        /* Stratégie :
            Parcourir la liste jusqu'à la fin
            Modifier l'avant-dernier noeud
            Ajuster le nombre d'éléments
            Retourner le contenu du dernier noeud
         */

        // gérer le cas où la liste est vide
        if (estVide()) {
            return null;
        }

        // gérer le cas où il y a un seul élément
        if (nbElement == 1) {
            Object contenu = tete.getElement();
            nbElement--;
            tete = null;
            return contenu;
        }

        // tous les autres cas
        Noeud courant = tete;
        Noeud precedent = tete;
        while (courant.getSuivant() != null) {
            precedent = courant;
            courant = courant.getSuivant();
        }

        // courant contient une référence vers le dernier noeud
        // precedent contient une référence vers l'avant-dernier noeud
        precedent.setSuivant(null);
        nbElement--;

        return courant.getElement();
    }

    /**
     * Retirer un élément au début de la liste et le retourner.
     * @return L'objet retiré, null si la liste est vide.
     */
    public Object retirerDebut() {
        // gérer le cas où la liste est vide
        if (nbElement == 0) {
            return null;
        }

        Object contenu = tete.getElement();
        nbElement--;

        // gérer le cas o il y avait un seul élément
        if (nbElement == 0) {
            tete = null;
        } else {
            tete = tete.getSuivant();
        }
        return contenu;
    }

    public String toString() {
        if (nbElement == 0) {
            return "Liste vide\n";
        }
        StringBuilder chaineListe = new StringBuilder();

        Noeud courant = tete;
        int position = 0;
        while (courant.getSuivant() != null) {
            chaineListe.append(position + " : " + courant.toString() + "\n");
            courant = courant.getSuivant();
            position++;
        }
        chaineListe.append(position + " : " + courant.toString() + "\n");

        return chaineListe.toString();
    }
}