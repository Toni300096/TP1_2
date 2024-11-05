package com.atoudeft.banque;

import java.io.Serializable;


/**
 * Cette classe implémente le noeud d'une liste chainée
 *
 * Le type de l'élément est abstrait en utilisant la classe Object
 */

public class Noeud implements Serializable {
    private final Object element;
    private Noeud suivant;


    // constructeur par paramètre
    public Noeud(Object element) {
        this.element = element;
    }

    // accesseurs
    public Noeud getSuivant() {
        return suivant;
    }

    public Object getElement() {
        return element;
    }

    // mutateurs
    public void setSuivant(Noeud suivant) {
        this.suivant = suivant;
    }

    public String toString() {
        return "Noeud : " + element.toString();
    }
}