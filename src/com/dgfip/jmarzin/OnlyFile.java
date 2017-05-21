package com.dgfip.jmarzin;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Cette classe est rendue nécessaire par la fonction
 * de sélection des fichiers de JFileChooser
 */
public class OnlyFile implements FilenameFilter {
    /**
     * l'extension du fichier
     */
    private String ext;
    /**
     * Crée une instance du sélectionneur de fichiers
     * @param ext l'extension à sélectionner
     */
    OnlyFile(String ext) {
        this.ext = "." + ext;
    }
    /**
     * Acceptation des fichiers qui se terminent par
     * l'extension. Le nom "accept" est imposé.
     * @param dir le répertoire à explorer
     * @param name le nom du fichier
     * @return vrai ou faux en fonction de l'extension
     */
    public boolean accept(File dir, String name) {
        return name.endsWith(ext);
    }
}
