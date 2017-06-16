package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.dgfip.jmarzin.ClicSie.jLabel;
import static com.dgfip.jmarzin.ClicSie.log;
/**
 * Cette classe représente le répertoire qui contient
 * les fichiers pdf à traiter.
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class RepertoireATraiter {
    /**
     * dictionnaire des repertoires à traiter
     */
    static List<RepertoireATraiter> listeRepertoires = new LinkedList<RepertoireATraiter>();
    /**
     * nombre total de fichiers passés en revue,
     * tous répertoires confondus.
     */
    private static int nbFichiers = 0;
    /**
     * le répertoire qui contient les fichiers à traiter
     */
    private File repertoire;
    File getRepertoire() { return repertoire;}
    /**
     * Indicateur de la nécessité d'ajouter une signature
     * au document avant envoi.
     */
    private boolean signatureNecessaire = false;
    boolean isSignatureNecessaire() {
        return signatureNecessaire;
    }
    /**
     * Contenu du fichier signature présent dans le répertoire
     * 1ere ligne pour le grade, 2ème ligne pour prénom nom.
     * Un répertoire est donc dédié à un signataire et un seul.
     */
    private String[] signature = null;
    String[] getSignature() {
        return signature;
    }
    /**
     * La liste des fichiers pdf à traiter. Il s'agit de la liste
     * des fichiers non exclus du traitement d'une part (les fichiers
     * résultats créés par un lancement précédent du programme ne
     * doivent pas être traités une deuxième fois) et dont le contenu
     * a été reconnu par identification du type de document.
     */
    private static List<FichierPdfATraiter> fichiersPdf = new ArrayList<FichierPdfATraiter>();
    static List<FichierPdfATraiter> getFichiersPdf() { return fichiersPdf;}
    /**
     * La liste des fichiers pdf qui seront déplacés en fin de traitement
     * dans le sous-répertoire "dejaTraites" existant ou créé dans le
     * repertoire en cours. Il s'agit des fichiers pdf à traiter autres
     * que les fichiers de verso, qui sont utilisés à chaque traitement,
     * et les fichiers résultats des traitements précédents.
     */
    private static List<File> fichiersADeplacer = new ArrayList<File>();
    static List<File> getFichiersADeplacer() { return fichiersADeplacer;}
    /**
     * Indique si un fichier pdf est exclus du traitement. Il s'agit
     * des fichiers résultats des traitements précédents, identifiés par
     * le début de leurs noms.
     *
     * @param nomFichier le nom du fichier
     * @return vrai ou faux suivant que le fichier est exclus ou pas
     */
    private boolean exclus(String nomFichier) {
        for(TypeActe typeActe : TypeActe.values()) {
            if (nomFichier.startsWith(typeActe.getNom() + "__")) return true;
        }
        return false;
    }
    /**
     * Etablit la liste des versos manquants, à partir
     * des types de documents identifiés
     * @return la liste des versos manquants
     */
    static Set<String> verifPresenceVerso() {
        Set<String> listeTypesDocument = new HashSet<String>();
        Set<String> listeVersosManquants = new HashSet<String>();
        for (FichierPdfATraiter fichier : fichiersPdf) {
            listeTypesDocument.add(fichier.getTypeFichier().getNom());
        }
        for(String type : listeTypesDocument) {
            String nomVerso = TypeDocument.get(type).getVersoInsere();
            if (nomVerso != null && !listeTypesDocument.contains(nomVerso)) {
                listeVersosManquants.add(nomVerso);
            }
        }
        return listeVersosManquants;
    }
    /**
     * Renvoie le fichier verso éventuel d'un type de document
     * @param typeDocument le type du document
     * @return le lecteur du fichier verso
     */
    PdfReader getVersoParNom(TypeDocument typeDocument) {
        for(FichierPdfATraiter fic : fichiersPdf) {
            if (fic.getTypeFichier() == typeDocument) return fic.getLecteurPdf();
        }
        return null;
    }
    /**
     * renvoie la liste des répertoires où la
     * signature devrait être présente et ne l'est pas
     */
    static List<File> verifSignatures() {
        List<File> listeSignaturesManquantes = new ArrayList<File>();
        for (RepertoireATraiter rep : listeRepertoires) {
            if (rep.isSignatureNecessaire() && rep.getSignature()== null) {
                listeSignaturesManquantes.add(rep.getRepertoire());
            }
        }
        return listeSignaturesManquantes;
    }
    /**
     * Crée l'instance de répertoire à traiter.
     * Reconnaît chaque fichier et enrichit la liste
     * des fichiers à déplacer après le traitement.
     * Lit la signature si un type de document le demande.
     * Signale les fichiers non reconnus.
     *
     * @param fc le résultat de la sélection du répertoire
     *           par l'utilsateur
     * @see #fichiersADeplacer
     */
    RepertoireATraiter(File fc) throws IOException {
        listeRepertoires.add(this);
        this.repertoire = fc.getAbsoluteFile();
        File[] listeFichiers = fc.listFiles(new Filtre("Fichiers"));
        int nbFichiersLocaux = 0;
        if(listeFichiers != null) nbFichiersLocaux = listeFichiers.length;
        for(int i = 0; i < nbFichiersLocaux; i++) {
            nbFichiers++;
            jLabel.setText((nbFichiers) + " fichier(s) traité()s");
            if(!exclus(listeFichiers[i].getName())) {
                FichierPdfATraiter fic = new FichierPdfATraiter(this, listeFichiers[i]);
                if (fic.getTypeFichier() == null) {
                    //la présence d'un fichier non reconnu est signalée. Il ne sera pas déplacé.
                    log(String.format("Le fichier %s n'est pas reconnu !", listeFichiers[i].getName()));
                } else {
                    fichiersPdf.add(fic);
                    TypeDocument typeFichier = fic.getTypeFichier();
                    if (!typeFichier.isVerso()) {
                        //on récupère la signature si un des types de documents le nécessite
                        if (typeFichier.getPlaceSignature() != null && typeFichier.getTypeActe().isClicEsiPlus()) {
                            signatureNecessaire = true;
                            try {
                                this.signature = Utilitaires.lit(repertoire.getCanonicalFile() + File.separator + "signature.txt");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //les fichiers reconnus autres que les versos seront déplacés
                        fichiersADeplacer.add(listeFichiers[i]);
                    }
                }
            } else {
                //les fichiers exclus seront déplacés
                fichiersADeplacer.add(listeFichiers[i]);
            }
        }
        File[] listeRepertoires = fc.listFiles(new Filtre("Repertoires"));
        for(File rep: listeRepertoires) {
            RepertoireATraiter repertoireATraiter = new RepertoireATraiter(rep);
        }
    }
}
