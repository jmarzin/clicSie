package com.dgfip.jmarzin;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe représente un lecteur de fichier de
 * paramétrage, interprété en yaml.
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class LecteurParametres {
    /**
     * Indicateur qu'une erreur a été rencontrée
     * pendant la lecture ou l'interprétation
     * du fichier des paramètres.
     */
    private boolean erreur = false;
    boolean isErreur() {
        return erreur;
    }
    /**
     * Création des rectangles où se situent
     * les adresses de l'expéditeur ou du destinataire.
     * Les coordonnées du fichier des paramètres étant
     * fournies en mm à partir du coin haut à gauche
     * de la page (conforme à LibreOffice Draw), elles
     * sont transformés en "points" à partir du bas à
     * droite de la page, utilisées par itext
     *
     * @param objetAdresse l'objet adresse lu
     * @return le rectangle construit
     */

    /**
     * Construit le lecteur de paramètres.
     * Les lignes sont lues dans un tableau,
     * transformé en un tableau de chaîne
     * comprenant chacune la structure yaml
     * d'un paramètre. Chaque chaîne est
     * ensuite interprétée.
     *
     * @param nomRepertoire le répertoire où se
     *                      trouve le fichier des
     *                      paramètres
     */
    LecteurParametres(String nomRepertoire) {
        //lecture du fichier des paramètres
        String[] lignes = null;
        try {
            lignes = Utilitaires.lit(nomRepertoire + File.separator + "ClicSie.params");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lignes == null) {
            erreur = true;
            return;
        }
        //constitution des chaînes yaml
        int index = -1;
        List<String> parametres = new ArrayList<String>();
        for (String sCurrentLine : lignes) {
            if(sCurrentLine.startsWith("--- ")) {
                parametres.add(sCurrentLine);
                index++;
            } else {
                parametres.set(index,parametres.get(index) + "\n" + sCurrentLine);
            }
        }
        //interprétation des chaînes yaml
        //et mise à jour des structures internes
        //des paramètres interprétés
        Yaml yaml = new Yaml();
        for(String instance : parametres) {
            Object objet = yaml.load(instance);
            if(objet.getClass() == TypeActe.class) {
            //    TypeActe cT = (TypeActe) objet;
            //    Map<String, TypeActe> dicoA = TypeActe.getDico();
            //    dicoA.put(cT.getNom(), cT);
            } else {
                TypeDocument cT = (TypeDocument) objet;
                //cT.setRectDest(creeRectangle(cT.getAdresseDest()));
                //cT.setRectExp(creeRectangle(cT.getAdresseExp()));
                //Map<String, TypeDocument> dicoD = TypeDocument.getDico();
                //dicoD.put(cT.getNom(), cT);
            }
        }
    }
}