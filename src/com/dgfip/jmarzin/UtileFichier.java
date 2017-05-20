package com.dgfip.jmarzin;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;

/**
 * Created by jmarzin-cp on 20/05/2017.
 */
public class UtileFichier {
    static String[] lit(String nomFichier) throws IOException {
        File fichier = new File (nomFichier);
        if (fichier.exists()) {
            byte[] fileData = new byte[(int) fichier.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(fichier));
            dis.readFully(fileData);
            dis.close();
            return new String(fileData, Charset.forName("UTF8")).split("\r\n");
        }
        return null;
    }
}
