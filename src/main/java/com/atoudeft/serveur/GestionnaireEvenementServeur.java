package com.atoudeft.serveur;

import com.atoudeft.banque.*;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;
import com.atoudeft.commun.evenement.Evenement;
import com.atoudeft.commun.evenement.GestionnaireEvenement;
import com.atoudeft.commun.net.Connexion;

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
                            cnx.envoyer("NOUVEAU NO " + t[0] + " existe");
                    }
                    break;
                case "CONNECT": //Se connecte à un compte préexistant et libre. (Tristan)
                    if (cnx.getNumeroCompteClient() != null) {
                        cnx.envoyer("CONNECT NO deja connecte");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
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

                case "EPARGNE": // créer un compte épargne pour le client
                    CompteClient compteClient = serveurBanque.getBanque().getCompteClient(cnx.getNumeroCompteClient());
                    boolean echoue = false;
                    if (cnx.getNumeroCompteClient() == null) { // vérifier que le client est connecté
                        echoue = true;
                    } else {
                        List<CompteBancaire> comptes = compteClient.getComptesBancaires();
                        for (int i = 0; i < comptes.size(); i++) {
                            if (comptes.get(i).getType().equals(TypeCompte.EPARGNE)) {
                                echoue = true;
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
                                }
                            }
                        }

                        CompteEpargne c = new CompteEpargne(num, TypeCompte.EPARGNE, 0.05);
                        compteClient.ajouter(c);
                    }
                    break;

                case "DEPOT": //depose de l'argent dans le compte client
                    //verifie si un client est connecte en regardant si il y a un numero de compte client
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("DEPOT NO");
                        break;
                    }
                    //recupere les arguments de l'evenement
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    //verifie si il y a au moins 1 argument etant le montant
                    if (t.length < 1) {
                        cnx.envoyer("DEPOT NO");
                        break;
                    }
                    //convertit le premier input en le montant a deposer
                    double montantDepot = Double.parseDouble(t[0]);
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    CompteClient compteDepot = banque.getCompteClient(numCompteClient);
                    compteDepot.deposer(montantDepot);

                    cnx.envoyer("DEPOT OK: " + montantDepot + "DEPOSE");
                    break;

                case "RETRAIT"://retirer de l'argent d'un compte
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
                    double montantRetrait = Double.parseDouble(t[0]);
                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    CompteClient compteRetrait = banque.getCompteClient(numCompteClient);
                    compteRetrait.retirer(montantRetrait);
                    cnx.envoyer("RETRAIT OK: " + montantRetrait + "RETIRE");
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
                    String description = String.join(" ", Arrays.copyOfRange(t,2,t.length));

                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    CompteClient compteFacture = banque.getCompteClient(numCompteClient);


                    //essayer de payer la facture
                    if (compteFacture.payerFacture(montantFacture, numeroFacture)) {
                        cnx.envoyer("FACTURE OK " + montantFacture + " payer pour la facture " + numeroFacture);
                    }
                    else{
                        cnx.envoyer("FACTURE NO");
                    }
                    break;
                case "HIST": //7.7 (TRISTAN)
                    //Obtenir le compte client + Verifier qu'il est belle et bien connecté
                    System.out.println("Demande pour Hist");
                    banque = serveurBanque.getBanque(); //Verification d'existence n'est pas encore implemente
                    if(banque==null) {
                        cnx.envoyer("HIST NO vous n'êtes pas connecté");
                        break;
                    }
                    //J'ai besoin de vérifier qu'un comptebanquaire seras trouvé avant de le mettre dans historique
                    List<CompteBancaire> compteBancaires= banque.getCompteClient(cnx.getNumeroCompteClient()).getComptesBancaires();
                    PileChainee historique = new PileChainee();
                    //Chercher le compte(setNumeroCompteActuel) dans comptes
                    for(CompteBancaire compteBancaire:compteBancaires){
                        if (compteBancaire.getNumero().equals(cnx.getNumeroCompteActuel())){
                            //Chercher l'attribut historique (une pile chaine)
                            historique = compteBancaire.getHistorique();
                            System.out.println("Found a compte");
                        }
                    }
                    //Écrire à l'utilisateur la liste de ses opérations passé.
                    System.out.println("Demande de print");
                    cnx.envoyer(historique.toString()); //Semble déjà implémenté et ne devrais pas poser problème.

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

                    numCompteClient = cnx.getNumeroCompteClient();
                    banque = serveurBanque.getBanque();
                    CompteClient compteTransfert = banque.getCompteClient(numCompteClient);
                    CompteClient compteDestinataire = banque.getCompteClient(numeroCompteDestinataire);

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