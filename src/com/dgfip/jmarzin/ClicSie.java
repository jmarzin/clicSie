package com.dgfip.jmarzin;


import com.itextpdf.text.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ClicSie {

    private static Set<TypeActe> ensembleEvenements = new HashSet<TypeActe>();
    static Set<TypeActe> getEnsembleEvenements() {
        return ensembleEvenements;
    }
    static void addEnsembleEvenements(TypeActe typeActe) {
        ensembleEvenements.add(typeActe);
    }

    //limite de pages
    static JLabel jLabel = new JLabel("Demande du répertoire"); // champ d'affichage des étapes
    private static JTextArea display = new JTextArea(16, 60);  // champ d'affichage de la log

    static void log(String texte) {
        System.out.println(texte);
        display.setText(display.getText()+texte+"\n");
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //fenêtre de suivi
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

        //Date-heure
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat formatDateHeure = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
        String dateHeure = formatDateHeure.format(now);

        //Choix du répertoire
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

        //Identification des fichiers PDF
        RepertoireATraiter repATraiter = new RepertoireATraiter(fc);

        //Vérification de la présence des versos nécessaires
        Set<String> nomVersosManquants = repATraiter.verifPresenceVerso();
        if(!nomVersosManquants.isEmpty()) {
            String message = "";
            for(String verso : nomVersosManquants) {
                message += " " + verso;
            }
            JOptionPane.showMessageDialog(null,
                    "Le ou les fichiers verso "+ message+ " sont absents.",
                    "Erreur",
                     JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //Transformation clic'esi ou pas
        Object[] options = {"Aucune", "ClicEsiPlus"};
        int n = JOptionPane.showOptionDialog(pan,
                "Choisissez la transformation à appliquer",
                "Transformation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default button title
        boolean clicEsi = (n == 1);

        //ouvrir le fichier de log
        String fichierLog = repATraiter.getRepertoire().getAbsolutePath() +
                File.separator + "atdSie__CR_" + dateHeure + ".txt";
        try {
            PrintStream stream = new PrintStream(fichierLog);
            System.setOut(stream);
            System.setErr(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Construction des preparateurFichiers
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

        //Appel de clic'esi plus
        if(clicEsi){
            for (String nomFichier: listeFichiers.keySet()) {
                jLabel.setText(String.format("Transformation du fichier %s", nomFichier));
                int numeroMethode;
                if(listeFichiers.get(nomFichier).get(0).getTypeDocument().getTypeActe().isUtiliseLO()) {
                    numeroMethode = 1;
                } else {
                    numeroMethode = 2;
                }
                try {
                    lotPrepare.clicEsi(nomFichier, numeroMethode,listeFichiers.get(nomFichier));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }
        log("Fin du traitement");
        jLabel.setText("Traitement terminé, consultez le compte-rendu ci-dessous.");
    }

}
