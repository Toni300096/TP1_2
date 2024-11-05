package com.programmes;

import com.atoudeft.serveur.Config;
import com.atoudeft.serveur.Serveur;
import com.atoudeft.banque.serveur.ServeurBanque;

import java.util.Scanner;

/**
 * Programme simple de d�monstration d'un serveur. Le programme d�marre un serveur qui se met � �couter
 * l'arriv�e de connexions.
 *
 * @author Abdelmoum�ne Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class ProgrammeServeurTP1 {
	/**
	 * M�thode principale du programme.
	 *
	 * @param args Arguments du programme
	 */
    public static void main(String[] args) {

        Scanner clavier = new Scanner(System.in);
        String saisie;

        Serveur serveur = new ServeurBanque(Config.PORT_SERVEUR);
        if (serveur.demarrer()) {
            System.out.println("Serveur a l'ecoute sur le port " + serveur.getPort());
        }

        System.out.println("Saisissez EXIT pour arreter le serveur.");        saisie = clavier.nextLine();
        while (!"EXIT".equals(saisie)) {
            System.out.println("??? Saisissez EXIT pour arreter le serveur.");
            saisie = clavier.nextLine();
        }
        serveur.arreter();
    }
}