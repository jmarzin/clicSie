package com.dgfip.jmarzin;

import com.itextpdf.text.Rectangle;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmarzin-cp on 14/05/2017.
 */
public class LecteurParametres {
    private static LecteurParametres ourInstance = new LecteurParametres();

    public static LecteurParametres getInstance() {
        return ourInstance;
    }

    private boolean erreur = false;
    public boolean isErreur() {
        return erreur;
    }

    LecteurParametres() {
    }

    LecteurParametres(String nomRepertoire) throws UnsupportedEncodingException {
        String[] lignes;
        try {
            lignes = UtileFichier.lit(nomRepertoire + File.separator + "ClicSie.params");
            if (lignes == null) {
                erreur = true;
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            erreur = true;
            return;
        }
        File fichierParametres = null;
        fichierParametres = new File(nomRepertoire + File.separator + "ClicSie.params");
        if (fichierParametres.exists()){
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fichierParametres),"UTF-8"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                erreur = true;
                return;
            }

            String sCurrentLine;
            List<String> parametres = new ArrayList<String>();
            int index = -1;
            try {
                while ((sCurrentLine = bufferedReader.readLine()) != null) {
                    if(sCurrentLine.startsWith("--- ")) {
                        index++;
                        parametres.add(sCurrentLine);
                    } else {
                        parametres.set(index,parametres.get(index) + "\n" + sCurrentLine);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                erreur = true;
                return;
            }
            Yaml yaml = new Yaml();
            for(String instance : parametres) {
                Object objet = yaml.load(instance);
                if(objet.getClass() == TypeActe.class) {
                    TypeActe cT = (TypeActe) objet;
                    Map<String, TypeActe> dicoA = TypeActe.getDico();
                    dicoA.put(cT.getNom(), cT);
                } else {
                    TypeDocument cT = (TypeDocument) objet;
                    if (cT.getAdresseDest() != null) {
                        LinkedHashMap<String, LinkedHashMap<String, Double>> adresse = (LinkedHashMap<String, LinkedHashMap<String, Double>>) cT.getAdresseDest();
                        Rectangle rect = new Rectangle(adresse.get("basGauche").get("x").floatValue()*72f/25.4f,
                                842f - adresse.get("basGauche").get("y").floatValue()*72f/25.4f,
                                adresse.get("hautDroite").get("x").floatValue()*72f/25.4f,
                                842f - adresse.get("hautDroite").get("y").floatValue()*72f/25.4f);
                        cT.setRectDest(rect);
                    }
                    if (cT.getAdresseExp() != null) {
                        LinkedHashMap<String, LinkedHashMap<String, Double>> adresse = (LinkedHashMap<String, LinkedHashMap<String, Double>>) cT.getAdresseExp();
                        Rectangle rect = new Rectangle(adresse.get("basGauche").get("x").floatValue()*72f/25.4f,
                                842f - adresse.get("basGauche").get("y").floatValue()*72f/25.4f,
                                adresse.get("hautDroite").get("x").floatValue()*72f/25.4f,
                                842f - adresse.get("hautDroite").get("y").floatValue()*72f/25.4f);
                        cT.setRectExp(rect);
                    }
                    Map<String, TypeDocument> dicoD = TypeDocument.getDico();
                    dicoD.put(cT.getNom(), cT);
                }
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                erreur = true;
            }
        } else {
            erreur = true;
        }
    }
}
