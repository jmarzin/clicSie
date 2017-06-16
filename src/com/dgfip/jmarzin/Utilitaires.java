package com.dgfip.jmarzin;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Cette classe comprend <ul><li>un utilitaire de lecture binaire
 * d'un fichier texte avec décodage UTF8. Elle est utilisée pour
 * la lecture du fichier des paramètres et du fichier de
 * l'adresse.</li>
 * <li>un utilitaire de concaténation de tableau de fichiers utilisé
 * pour explorer les sous-répertoires du réperoitre à traitre</li></ul>
 * @author Jacques Marzin
 * @version 1.0
 * @since 20 mai 2017
 */
public class Utilitaires {
    /**
     * lit la totalité d'un fichier et éclate son contenu
     * en lignes, en utilisant la séquence de séparation
     * '\r\n'.
     * @param nomFichier le nom du fichier à lire
     * @return le tableau des lignes lues
     * @throws IOException Pb de lecture
     */
    static String[] lit(String nomFichier) throws IOException {
        File fichier = new File (nomFichier);
        if (fichier.exists()) {
            byte[] fileData = new byte[(int) fichier.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(fichier));
            dis.readFully(fileData);
            dis.close();
            return new String(fileData, Charset.forName("UTF8")).replaceAll("\uFEFF","").split("\r\n");
        } else {
            return null;
        }
    }
    /**
     * concatene deux tableaux d'objets de type File
     * @param tableau1 le premier tableau
     * @param tableau2 le deuxième tableau
     * @return le tableau résultat de la concaténation
     */
    static File[] copy(File[] tableau1, File[] tableau2) {
        File[] tableau3 = new File[tableau1.length + tableau2.length];
        int index = -1;
        for (int i = 0; i < tableau1.length; i++) {
            tableau3[++index] = tableau1[i];
        }
        for (int i = 0; i < tableau2.length; i++) {
            tableau3[++index] = tableau2[i];
        }
        return tableau3;
    }
}
