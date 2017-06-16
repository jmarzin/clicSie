package com.dgfip.jmarzin;

import java.io.File;
import java.io.FileFilter;

/**
 * Cette classe est rendue nécessaire par la fonction
 * de sélection des fichiers de JFileChooser
 */
public class Filtre implements FileFilter {
    /**
     * l'extension du fichier
     */
    private String typeFic;
    /**
     * Crée une instance du sélectionneur de fichiers
     * @param typeFic donne le type de fichier à appeler, fichiers
     *           ou répertoire
     */
    Filtre(String typeFic) {
        this.typeFic = typeFic;
    }
    /**
     * Acceptation des fichiers qui se terminent par
     * l'extension. Le nom "accept" est imposé.
     * @param file l'entrée à tester
     * @return vrai ou faux en fonction de l'entrée et
     * du contexte
     */
    public boolean accept(File file)
    {
        if(typeFic.equals("Fichiers")) {
            return file.getName().endsWith(".pdf");
        } else { // cas des répertoires
            return file.isDirectory() && !file.getName().equals("dejaTraites");
        }
    }
}
