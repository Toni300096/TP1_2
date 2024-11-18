package com.atoudeft.serveur;

import com.atoudeft.banque.*;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;
import com.atoudeft.commun.evenement.Evenement;
import com.atoudeft.commun.evenement.GestionnaireEvenement;
import com.atoudeft.commun.net.Connexion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cette classe représente un gestionnaire d'événement d'un serveur. Lorsqu'un serveur reçoit un texte d'un client,
 * il crée un événement à partir du texte reçu et alerte ce gestionnaire qui réagit en gérant l'événement.
 *
 * @author Abdelmoumène Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class GestionnaireEvenementServeur implements GestionnaireEvenement {
    private Serveur serveur;

    /**
     * Construit un gestionnaire d'événements pour un serveur.
     *
     * @param serveur Serveur Le serveur pour lequel ce gestionnaire gère des événements
     */
    public GestionnaireEvenementServeur(Serveur serveur) {
        this.serveur = serveur;
    }

    /**
     * Méthode de gestion d'événements. Cette méthode contiendra le code qui gère les réponses obtenues d'un client.
     *
     * @param evenement L'événement à gérer.
     */
    @Override
    public void traiter(Evenement evenement) {
        Object source = evenement.getSource();
        ServeurBanque serveurBanque = (ServeurBanque) serveur;
        Banque banque;
        ConnexionBanque cnx;
        String msg, typeEvenement, argument, numCompteClient, nip;
        String[] t;
        CompteBancaire compteBancaire;

        if (source instanceof Connexion) {
            cnx = (ConnexionBanque) source;
            System.out.println("SERVEUR: Recu : " + evenement.getType() + " " + evenement.getArgument());
            typeEvenement = evenement.getType();
            cnx.setTempsDerniereOperation(System.currentTimeMillis());
            switch (typeEvenement) {

                /******************* COMMANDES GÉNÉRALES *******************/
                case "EXIT": //Ferme la connexion avec le client qui a envoyé "EXIT":
                    cnx.envoyer("END");
                    serveurBanque.enlever(cnx);
                    cnx.close();
                    break;
                case "LIST": //Envoie la liste des numéros de comptes-clients connectés :
                    cnx.envoyer("LIST " + serveurBanque.list());
                    break;
                /******************* COMMANDES DE GESTION DE COMPTES *******************/
                case "NOUVEAU": //Crée un nouveau compte-client :
                    if (cnx.getNumeroCompteClient() != null) {
                        cnx.envoyer("NOUVEAU NO deja connecté");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length < 2) {
                        cnx.envoyer("NOUVEAU NO");
                    } else {
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        if (banque.ajouter(numCompteClient, nip)) {
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("NOUVEAU OK " + t[0] + " cree");
                        } else
                            cnx.envoyer("NOUVEAU NO " + t[0] + " ne peut pas etre creer");
                    }
                    break;
                case "CONNECT": //Se connecte à un compte préexistant et libre. (Tristan)
                    if (cnx.getNumeroCompteClient() != null) {
                        cnx.envoyer("CONNECT NO deja connecte");
                        break;
                    }
                    banque = serveurBanque.getBanque();
                    //verifie qu'il y a au moins 1 compte cree
                    if (banque.getComptesClient().isEmpty()){
                        cnx.envoyer("CONNECT NO aucun compte existant");
                        break;
                    }

                    argument = evenement.getArgument();
                    t = argument.split(":");
                    //verifie si les arguments ont les
                    if(t.length < 2) {
                        cnx.envoyer("CONNECT NO arguments invalides");
                        break;
                    }

                    //Vérifie qu'aucune des ConnexionBanque déjà connectés utilise le même numéro de compte.
                    numCompteClient = t[0];
                    nip = t[1];
                    for (Connexion cny : serveur.connectes) {
                        if (cny instanceof ConnexionBanque && t[0].equals(((ConnexionBanque) cny).getNumeroCompteClient())) {
                            cnx.envoyer("CONNECT NO " + numCompteClient + " deja utilise");
                            break;
                        }
                    }
                    banque = serveurBanque.getBanque();
                    //Récupérez de la banque le compte-client et vérifiez que le nip est correct. Si
                    //le compte n’existe pas ou le nip est incorrect, le serveur refuse la demande et
                    //envoie la réponse CONNECT NO au client;
                    CompteClient compteTest = banque.getCompteClient(numCompteClient);

                    if (compteTest == null || !compteTest.verifierNip(nip)) {
                        cnx.envoyer("CONNECT NO mauvaises informations");
                        break;
                    }
                    //Vous inscrivez le numéro du compte-client et le numéro de son compte
                    //chèque dans l’objet ConnexionBanque du client. Le serveur envoie la réponse
                    //CONNECT OK.
                    cnx.setNumeroCompteClient(numCompteClient);
                    cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                    cnx.envoyer("CONNECT OK compte " + t[0] + " maintenant connecté");
                    break;

                case "SELECT":
                    if (cnx.getNumeroCompteClient() != null) {
                        argument = evenement.getArgument();
                        if (argument == null) {
                            cnx.envoyer("SELECT NO argument invalide");
                            break;
                        }
                        numCompteClient = cnx.getNumeroCompteClient();
                        banque = serveurBanque.getBanque();
                        List<CompteBancaire> cb = banque.getCompteClient(numCompteClient).getComptesBancaires(); // obtenir le compte client
                        boolean trouve = false;
                        for (CompteBancaire bancaire : cb) { // itérer à travers des comptes bancaires du client pour trouver le compte recherché
                            if (argument.equals(TypeCompte.EPARGNE.toString())) {
                                if (bancaire.getType().equals(TypeCompte.EPARGNE)) {
                                    cnx.setNumeroCompteActuel(bancaire.getNumero());
                                    cnx.envoyer("SELECT OK compte epargne");
                                    trouve = true;
                                }
                            } else if (argument.equals(TypeCompte.CHEQUE.toString())) {
                                if (bancaire.getType().equals(TypeCompte.CHEQUE)) {
                                    cnx.setNumeroCompteActuel(bancaire.getNumero());
                                    cnx.envoyer("SELECT OK compte cheque");
                                    trouve = true;
                                }
                            }
                        }
                        if (!trouve) { // si le compte n'est pas trouvé
                            cnx.envoyer("SELECT NO compte non trouvé");
                        }
                        break;
                    }
                    cnx.envoyer("SELECT NO utilisateur non connecté");
                    break;
                case "EPARGNE": // créer un compte épargne pour le client
                    CompteClient compteClient = serveurBanque.getBanque().getCompteClient(cnx.getNumeroCompteClient());
                    boolean echoue = false;
                    if (cnx.getNumeroCompteClient() == null) { // vérifier que le client est connecté
                        echoue = true;
                    } else {
                        List<CompteBancaire> comptes = compteClient.getComptesBancaires();
                        for (CompteBancaire compte : comptes) {
                            if (compte.getType().equals(TypeCompte.EPARGNE)) {
                                echoue = true;
                                break;
                            }
                        }
                    }

                    if (echoue) {
                        cnx.envoyer("EPARGNE NO");
                    } else { // si les exceptions ont été passées, créer un compte épargne avec un numéro unique.
                        String num = null;
                        boolean recherche = true;
                        while (recherche) {
                            recherche = false;
                            num = CompteBancaire.genereNouveauNumero();
                            for (int i = 0; i < serveurBanque.getBanque().getComptesClient().size(); i++) {
                                if (serveurBanque.getBanque().getComptesClient().get(i).getNumero().equals(num)) {
                                    recherche = true;
                                    break;
                                }
                            }
                        }

                        CompteEpargne c = new CompteEpargne(num, TypeCompte.EPARGNE, 0.05);
                        compteClient.ajouter(c);
                    }
                    break;

                case "DEPOT": //depose de l'argent dans le compte client
                    try {
                        //verifie si un client est connecte en regardant si il y a un numero de compte client
                        if (cnx.getNumeroCompteClient() == null) {
                            cnx.envoyer("DEPOT NO : Client non connecte");
                            break;
                        }

                        //recupere les arguments de l'evenement
                        argument = evenement.getArgument();

                        //convertit l'input en montant déposer
                        double montantDepot;
                        try {
                            montantDepot = Double.parseDouble(argument);
                        } catch (NumberFormatException e) {
                            cnx.envoyer("DEPOT NO: montant invalide");
                            break;
                        }
                        compteBancaire= serveurBanque.getBanque().getCompteBancaireActif(cnx);
                        if(compteBancaire==null) {
                            cnx.envoyer(typeEvenement + " NO : compte bancaire non trouve");
                            break;
                        }
                        compteBancaire.deposer(montantDepot);

                        cnx.envoyer("DEPOT OK: " + montantDepot + "DEPOSE");
                    }
                    catch(IllegalArgumentException e) {
                        cnx.envoyer("DEPOT NO: " + e.getMessage());
                    }
                    catch(Exception e){
                        cnx.envoyer("DEPOT NO: une erreur est survenue");
                        e.printStackTrace(); //pour debug les erreurs (a effacer apres)
                    }
                    break;

                case "RETRAIT"://retirer de l'argent d'un compte
                    try {
                        //verifie si un client est connecte en regardant si il y a un numero de compte client
                        if (cnx.getNumeroCompteClient() == null) {
                            cnx.envoyer("RETRAIT NO");
                            break;
                        }
                        argument = evenement.getArgument();
                        t = argument.split(":");
                        //verifie si il y a au moin s 1 argument
                        if (t.length < 1) {
                            cnx.envoyer("RETRAIT NO");
                            break;
                        }
                        //convertit le premier input en le montant a retirer
                        double montantRetrait;
                        try {
                            montantRetrait = Double.parseDouble(t[0]);
                        } catch (NumberFormatException e) {
                            cnx.envoyer("RETRAIT NO: montant invalide");
                            break;
                        }
                        compteBancaire= serveurBanque.getBanque().getCompteBancaireActif(cnx);
                        if(compteBancaire==null) {
                            cnx.envoyer(typeEvenement + " NO : compte bancaire non trouve");
                            break;
                        }
                        compteBancaire.retirer(montantRetrait);
                        cnx.envoyer("RETRAIT OK: " + montantRetrait + "RETIRE");
                    }
                    catch (IllegalArgumentException e) {
                        cnx.envoyer("RETRAIT NO: " + e.getMessage());
                    } catch (Exception e) {
                        cnx.envoyer("RETRAIT NO: une erreur est survenue");
                        e.printStackTrace(); // Debugging purposes
                    }
                    break;
                case "FACTURE": //payer une facture
                    //verifie la connection du client
                    if (cnx.getNumeroCompteClient() == null){
                        cnx.envoyer("FACTURE NO");
                        break;
                    }

                    argument = evenement.getArgument();
                    t = argument.split(":");

                    //verifie qu'il y a 3 arguments (montant, # facture, description)
                    if(t.length < 3){
                        cnx.envoyer("FACTURE NO");
                        break;
                    }

                    double montantFacture = Double.parseDouble(t[0]);
                    String numeroFacture = t[1];
                    String descriptionFacture = t[2];

                    compteBancaire= serveurBanque.getBanque().getCompteBancaireActif(cnx);
                    if(compteBancaire==null) {
                        cnx.envoyer(typeEvenement + " NO : compte bancaire non trouve");
                        break;
                    }


                    //essayer de payer la facture
                    if (compteBancaire.payerFacture(montantFacture, numeroFacture, descriptionFacture)) {
                        cnx.envoyer("FACTURE OK " + montantFacture + " payer pour la facture " + numeroFacture);
                    }
                    else{
                        cnx.envoyer("FACTURE NO");
                    }
                    break;
                case "HIST": //7.7 (TRISTAN)
                    //J'ai besoin de vérifier qu'un comptebanquaire seras trouvé avant de le mettre dans historique
                    compteBancaire= serveurBanque.getBanque().getCompteBancaireActif(cnx);
                    if(compteBancaire==null) {
                        cnx.envoyer(typeEvenement + " NO : compte bancaire non trouve");
                        break;
                    }
                    //Cherche l'historique
                    PileChainee historique = compteBancaire.getHistorique();
                    if(historique==null || historique.estVide() ) {
                        cnx.envoyer("HIST NO vous n'avez pas d'historique");
                        break;
                    }
                    //Écrire à l'utilisateur la liste de ses opérations passé.
                    cnx.envoyer(historique.toString()); //Semble déjà implémenté et ne devrais pas poser problème. Pose des problèmes :(

                    break;
                case "TRANSFER": //transferer des fonds vers un autre compte
                    //verifie connection client
                    if(cnx.getNumeroCompteClient() == null){
                        cnx.envoyer("FACTURE NO");
                        break;
                    }

                    argument = evenement.getArgument();
                    t = argument.split(":");

                    if (t.length < 2){
                        cnx.envoyer("TRANSFER NO");
                        break;
                    }

                    double montantTransfer = Double.parseDouble(t[0]);
                    String numeroCompteDestinataire = t[1];

                    banque = serveurBanque.getBanque();
                    CompteBancaire compteTransfert= banque.getCompteBancaireActif(cnx);
                    CompteBancaire compteDestinataire = banque.getCompteBancaire(cnx,numeroCompteDestinataire);
                    if(compteTransfert==null || compteDestinataire==null) {
                        cnx.envoyer(typeEvenement + " NO : compte bancaire non trouve");
                        break;
                    }


                    if (compteTransfert.transferer(montantTransfer, compteDestinataire)){
                        cnx.envoyer("TRANFERT OK: " + montantTransfer + "transfere au compte " + numeroCompteDestinataire);
                    }
                    else{
                        cnx.envoyer("TRANFERT NO");
                    }
                    break;

                /******************* TRAITEMENT PAR DÉFAUT *******************/
                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}