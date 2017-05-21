package com.dgfip.jmarzin;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static com.dgfip.jmarzin.ClicSie.jLabel;
import static com.dgfip.jmarzin.ClicSie.log;

/**
 * Cette classe représente le lot complet de courriers
 * à traiter. Il comprend un dictionnaire des lots par
 * acte, un par Type d'acte. Chacun d'eux comprend un
 * dictionnaire des lots par Fichier Produit, un par
 * type de fichier produit. Chacun de ceux-ci comprend
 * un dictionnaire des lots par courrier, un par clé
 * identifiant un destinataire ou une procédure unique.
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class LotPrepare {
    /**
     * Nombre total de pages lues
     */
    private int nbPages = 0;
    /**
     * Dictionnaire par type d'acte des lots par acte
     */
    private Map<TypeActe, LotParActe> dicoTypesActe = new HashMap<TypeActe, LotParActe>();
    /**
     * Crée un nouveau lot vide
     */
    LotPrepare() {
    }
    /**
     * Ajoute un document au lot global. Si le type
     * d'acte du document existe déjà dans le dictionnaire,
     * ajoute le document au lot par acte correspondant.
     * Sinon, crée le lot par acte et y ajoute le
     * document. Met à jour le nombre de pages.
     *
     * @param documentAdm le document à ajouter
     */
    void ajout(DocumentAdm documentAdm) {
        if(documentAdm.getNbPages() == 0) return;
        if (dicoTypesActe.containsKey(documentAdm.getTypeActe())) {
            dicoTypesActe.get(documentAdm.getTypeActe()).ajout(documentAdm);
        } else {
            dicoTypesActe.put(documentAdm.getTypeActe(), new LotParActe(documentAdm));
        }
        nbPages += documentAdm.getNbPages();
    }
    /**
     * Vérifie le lot global et appelant la vérification de chaque
     * lot par acte qu'il contient.
     */
    void verif() {
        for (LotParActe lotParActe : dicoTypesActe.values()) {
            lotParActe.verif();
        }
    }
    /**
     * Ecrit la globalité des courriers, en demandant l'écriture
     * de chacun des lots par acte qu'il contient.
     *
     * @param listeFichiers liste des fichiers et des pages à modifier
     *                      à l'occasion de la mise aux normes
     * @param repertoireATraiter instance de répertoire qui contient
     *                           les fichiers à traiter
     * @param dateHeure groupe date-heure utilisé pour les noms
     *                  de fichiers
     * @return la liste des fichiers à jour avec leurs pages à modifier
     * @throws IOException Pb d'écriture d'une page
     * @throws DocumentException Pg d'écriture d'une page lue
     */
    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                           RepertoireATraiter repertoireATraiter,
                                           String dateHeure) throws IOException, DocumentException {
        for (LotParActe lotParActe : dicoTypesActe.values()) {
            listeFichiers = lotParActe.ecrit(listeFichiers, repertoireATraiter, dateHeure);
        }
        return listeFichiers;
    }
    /**
     * Mise aux normes clicesiPlus d'un fichier, à partir du
     * paramétrage et de la liste des pages qu'il faut modifier.
     * Le fichier d'origine est supprimé.
     *
     * @param nomFichier le nom du fichier à mettre aux normes
     * @param pages la liste des pages à modifier
     * @param signature la signature à utiliser
     * @return le nom du fichier aux normes
     * @throws IOException Pb lors de la récupération ou de la
     *                      suppression des zones adresses
     * @throws DocumentException Mauvaise lecture des fichiers de fontes
     *                      ou pb à la suppression des zones adresses
     */
    String clicEsi(String nomFichier, List<PageAModifier> pages, String[] signature) throws IOException, DocumentException {
        Clicesiplus clic = new Clicesiplus(nomFichier);
        for (PageAModifier page : pages) {
            int i = page.getIpage();
            String[] texte1 = null;
            String[] texte2 = null;
            jLabel.setText(String.format("Fichier %s, pages converties : %d/%d",
                    nomFichier.substring(nomFichier.lastIndexOf('\\') + 1), i, clic.getNbTotalPages()));
            //récupérer l'adresse du SIE si nécessaire
            if (page.getTypeDocument().getRectExp() != null) {
                texte1 = clic.getAdresse(TypeAdresse.Exp, page);
                texte1[1] += " - recouvrement";
            }
            //récupérer l'adresse du destinataire si nécessaire
            if (page.getTypeDocument().getRectDest() != null) {
                texte2 = clic.getAdresse(TypeAdresse.Dest, page);
            }
            //effacer l'adresse expéditeur si nécessaire
            if (page.getTypeDocument().isDeleteExp()) clic.deleteAdresse(TypeAdresse.Exp, page);
            //effacer l'adresse destinataire si nécessaire
            if (page.getTypeDocument().isDeleteDest()) clic.deleteAdresse(TypeAdresse.Dest, page);
            //replacer l'adresse SIE si nécessaire
            if (page.getTypeDocument().getRectExp() != null) clic.replaceAdresse(TypeAdresse.Exp, texte1, i);
            //replacer l'adresse destinataire
            if (page.getTypeDocument().getRectDest() != null) clic.replaceAdresse(TypeAdresse.Dest, texte2, i);
            //mettre la date
            Map<String,Float> placeDate = page.getTypeDocument().getPlaceDate();
            if (placeDate != null) clic.placeDate(placeDate, i);
            //mettre la signature
            Map<String, Float> placeSignature = page.getTypeDocument().getPlaceSignature();
            if (placeSignature != null) {
                clic.placeSignature(placeSignature, page.getTypeDocument().isAvecGrade(), signature, i);
            }
            //mettre les trois dièses
            if (page.isRupture()) {
                clic.diese(i);
            }
        }
        clic.close();
        File fichier = new File(nomFichier);
        if (!fichier.delete()) log(String.format("Suppression de %s impossible", fichier.getName()));
        return clic.getNomFichierProduit();
    }
    /**
     * Transmet à Libre Office le fichier indiqué pour traitement
     * @param nomFichier le nom du fichier à transmettre
     */
    void libreOffice(String nomFichier) {
        String[] commande = new String[]{"C:\\Program Files\\LibreOffice 4\\program\\sdraw",
                nomFichier,
                "macro:///Standard.ClicEsi.ClicEsiPlus()"};
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(commande);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File fichier = new File(nomFichier);
        if (!fichier.delete()) log(String.format("Suppression de %s impossible", fichier.getName()));
    }
}
