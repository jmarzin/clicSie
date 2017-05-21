package com.dgfip.jmarzin;


import com.itextpdf.text.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ClicSie {
    /**
     * Champ public permettant d'afficher l'étape du traitement
     */
    static JLabel jLabel = new JLabel("Demande du répertoire"); // champ d'affichage des étapes
    /**
     * Zone d'affichage de la log à l'écran
     */
    private static JTextArea display = new JTextArea(16, 60);  // champ d'affichage de la log
    /**
     * Affiche un texte dans la log et sur la sortie par défaut
     * @param texte le texte à afficher
     */
    static void log(String texte) {
        System.out.println(texte);
        display.setText(display.getText()+texte+"\n");
    }
    /**
     * Corps du traitement
     * Il réalise en séquence les opérations suivantes :
     *  - initialise la fenêtre de suivi
     *  - initialise le groupe date-heure utilisé pour les fichiers
     *  - demande à l'utilisateur le répertoire à traiter
     *  - vérifie et charge le fichier des paramètres
     *  - identifie les fichiers à traiter et ceux à déplacer à la fin
     *  - vérifie la présence des versos éventuellement nécessaires
     *  - vérifie la présence du fichier de signature s'il est nécessaire
     *  - ouvre le fichier de log (ClicSie__CR_...)
     *  - explore les fichiers à traiter et prépare le lot à écrire
     *  - écrit les fichiers à partir du lot préparé
     *  - ferme les fichiers à traiter
     *  - déplace les fichiers à déplacer dans le ss/répertoire
     *      dejaTraites
     *  - met au format ClicEsiPlus les fichiers qui le nécessitent
     *  - transmet à LibreOffice les fichiers qui le nécessitent
     * @author Jacques Marzin
     * @since 20 mai 2017
     */
    public static void main(String[] args) {
        //Initialisation de la fenêtre de suivi
        JFrame fenetre = new JFrame();
        fenetre.setTitle("Traitement des preparateurFichiers d'ATD");
        fenetre.setSize(700, 350);
        fenetre.setLocationRelativeTo(null);
        fenetre.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fenetre.setVisible(true);
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan,BoxLayout.PAGE_AXIS));
        fenetre.setContentPane(pan);
        pan.setVisible(true);
        jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pan.add(jLabel);
        display.setEditable(false); // set textArea non-editable
        display.setVisible(true);
        JScrollPane scroll = new JScrollPane(display);
        display.setAlignmentX(Component.CENTER_ALIGNMENT);
        pan.add(scroll);
        //Initialisation de la date et de l'heure utilisées dans les noms de fichiers créés
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat formatDateHeure = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
        String dateHeure = formatDateHeure.format(now);
        //Choix du répertoire contenant les fichiers à traiter
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(pan);
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        }
        //Vérification et chargement du fichier des paramètres
        LecteurParametres lecteurParametres = new LecteurParametres(fc.getSelectedFile().getAbsolutePath());
        if(lecteurParametres.isErreur()) {
            JOptionPane.showMessageDialog(null,
                    "Le fichier ClicSie.params est absent.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        //Identification des fichiers PDF à traiter et à déplacer en fin
        //de traitements
        RepertoireATraiter repATraiter = new RepertoireATraiter(fc);
        //Vérification de la présence des versos nécessaires
        Set<String> nomVersosManquants = repATraiter.verifPresenceVerso();
        if(!nomVersosManquants.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for(String verso : nomVersosManquants) {
                message.append(" ").append(verso);
            }
            JOptionPane.showMessageDialog(null,
                    "Le ou les fichiers verso "+ message+ " sont absents.",
                    "Erreur",
                     JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        //vérification de la présence du fichier des signatures
        if(repATraiter.isSignatureNecessaire() && repATraiter.getSignature() == null) {
            JOptionPane.showMessageDialog(null,
                    "Le fichier signature.text est absent.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        //ouvrir le fichier des log
        String fichierLog = repATraiter.getRepertoire().getAbsolutePath() +
                File.separator + "ClicSie__CR_" + dateHeure + ".txt";
        try {
            PrintStream stream = new PrintStream(fichierLog);
            System.setOut(stream);
            System.setErr(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Construction du lot de création de fichiers
        PreparateurFichiers preparateurFichiers = new PreparateurFichiers(repATraiter);
        LotPrepare lotPrepare = preparateurFichiers.getLotPrepare();
        //Vérification du lot préparé
        lotPrepare.verif();
        //Ecriture des fichiers
        Map<String,List<PageAModifier>> listeFichiers = new HashMap<String, List<PageAModifier>>();
        try {
            listeFichiers = lotPrepare.ecrit(listeFichiers, repATraiter, dateHeure);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //fermeture des fichiers
        for (FichierPdfATraiter fichier : repATraiter.getFichiersPdf()) {
            fichier.getLecteurPdf().close();
        }
        //déplacement des fichiers
        jLabel.setText("Déplacement des fichiers");
        AtomicReference<File> repTraites = new AtomicReference<File>();
        try {
            repTraites.set(new File(repATraiter.getRepertoire().getCanonicalPath() +
                    File.separatorChar + "dejaTraites"));
            if(!repTraites.get().exists() || repTraites.get().isFile()) {
                if(!repTraites.get().mkdir()) log("Création du répertoire dejaTraites impossible");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String fichier : repATraiter.getFichiersADeplacer()) {
            String nomOr = repATraiter.getRepertoire().getAbsolutePath() + File.separator + fichier;
            String nomDest = repATraiter.getRepertoire().getAbsolutePath() + File.separator +
                    "dejaTraites" + File.separator + fichier;
            if(!new File(nomOr).renameTo(new File(nomDest))) log(String.format("Rename de %s impossible", nomOr));
        }
        //Appel de clic'esi pour les fichiers qui le nécessite
        Map<String, TypeActe> listeFichiers2 = new HashMap<String, TypeActe>();
        for (String nomFichier: listeFichiers.keySet()) {
            TypeActe typeActe = listeFichiers.get(nomFichier).get(0).getTypeDocument().getTypeActe();
            if (typeActe.isClicEsiPlus()) {
                jLabel.setText(String.format("Transformation du fichier %s", nomFichier));
                try {
                    String nomFichier2 = lotPrepare.clicEsi(nomFichier, listeFichiers.get(nomFichier),repATraiter.getSignature());
                    listeFichiers2.put(nomFichier2, typeActe);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            } else {
                listeFichiers2.put(nomFichier, typeActe);
            }
        }
        //Appel de Libre Office pour les fichiers qui le nécessite
        for (String nomFichier: listeFichiers2.keySet()) {
            if (listeFichiers2.get(nomFichier).isUtiliseLO()) {
                jLabel.setText(String.format("Envoi du fichier %s à LibreOffice", nomFichier));
                lotPrepare.libreOffice(nomFichier);
            }
        }
        log("Fin du traitement");
        jLabel.setText("Traitement terminé, consultez le compte-rendu ci-dessous.");
    }
}
