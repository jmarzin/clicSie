package com.dgfip.jmarzin;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.dgfip.jmarzin.ClicSie.jLabel;
/**
 * Cette classe représente un lot de courriers pour un
 * type de fichier (à mettre sous plis ou à conserver
 * dans le service). Il contient un dictionnaire
 * des courriers qu'il renferme, chacun étant identifié
 * par une clé. La classe représente aussi un fichier
 * logique, qui sera éventuellement découpé en plusieurs
 * fichiers physiques en fonction du nombre de pages et
 * du paramétrage.
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class FichierProduit {
    /**
     * Nombre de pages du lot de courriers
     */
    private int nbPages = 0;
    /**
     * Type d'acte du lot
     * @see TypeActe
     */
    private TypeActe typeActe;
    /**
     * Type de fichier produit du lot. Il
     * y a un lot par type de fichier.
     */
    private TypeFichierProduit typeFichierProduit;
    /**
     * dictionnaire des courriers identifiés par leur clé
     * alphanumérique
     */
    private Map<String, Courrier> dicoCles = new HashMap<String, Courrier>();
    /**
     * Construit un lot et y range un premier document
     * @param documentAdm le document à y mettre
     */
    FichierProduit(DocumentAdm documentAdm) {
        this.typeActe = documentAdm.getTypeActe();
        this.typeFichierProduit = documentAdm.getTypeFichierProduit();
        ajout(documentAdm);
    }
    /**
     * Ajoute un document à un lot existant.
     * Si la clé est déjà dans le dictionnaire,
     * on ajoute le document au courrier correspondant.
     * Si la clé n'existe pas, on crée le courrier
     * et on y ajoute le document.
     *
     * @param documentAdm le document à ajouter
     */
    void ajout(DocumentAdm documentAdm) {
        if (dicoCles.containsKey(documentAdm.getCle())) {
            dicoCles.get(documentAdm.getCle()).ajout(documentAdm);
        } else {
            dicoCles.put(documentAdm.getCle(), new Courrier(documentAdm));
        }
        nbPages += documentAdm.getNbPages();
    }
    /**
     * Vérifie le contenu du lot en appelant la
     * vérification de tous les courriers qu'il
     * contient
     */
    void verif() {
        for (Courrier courrier : dicoCles.values()) {
            courrier.verif();
        }
    }
    /**
     * Ecrit le lot. C'est le paramétrage type d'acte qui
     * définit le nombre maximum de pages d'un fichier écrit.
     * Au-delà, plusieurs parties sont créées.
     * Les courriers sont écrits dans l'ordre des clés.
     * @param listeFichiers liste des fichiers résultats avec
     *                      la liste des pages qui seront à
     *                      mettre aux normes clicesiplus
     * @param repertoireATraiter instance représentant le
     *                           répertoire des fichiers à traiter
     * @param dateHeure groupe date-heure pour les noms de fichiers
     * @return la liste des fichiers résultats à jour des nouveaux fichiers
     * @throws IOException Pb d'écriture de page
     * @throws DocumentException Pb d'écriture de page lue
     */
    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                                  RepertoireATraiter repertoireATraiter,
                                                  String dateHeure) throws IOException, DocumentException {
        Document doc = new Document();
        PdfSmartCopy copy;
        String nomFichier;
        int nbPagesTraitees = 0;
        //en fonction du nombre maximum de pages, le nom du premier fichier est différent
        if (typeActe.getMaxPages() == 0 || nbPages <= typeActe.getMaxPages()) {
            nomFichier = repertoireATraiter.getRepertoire().getCanonicalPath() + File.separatorChar +
                    typeActe.getNom() + "__" + typeFichierProduit.name() + "_" + dateHeure + ".pdf";
        } else {
            nomFichier = repertoireATraiter.getRepertoire().getCanonicalPath() + File.separatorChar +
                    typeActe.getNom() + "__" + typeFichierProduit.name() + "_partie_1_" + dateHeure + ".pdf";
        }
        //initialisation de la liste des pages à modifier pour le fichier
        listeFichiers.put(nomFichier, new ArrayList<PageAModifier>());
        copy = new PdfSmartCopy(doc, new FileOutputStream(nomFichier));
        doc.open();
        int partie = 1;
        //tri des clés
        Object[] clesTriees = dicoCles.keySet().toArray();
        Arrays.sort(clesTriees);
        //pour chaque clé, on écrit le courrier correspondant
        for (Object cle : clesTriees) {
            //si à la fin d'un courrier on a dépassé le nombre de pages max, on crée un nouveau fichier
            if (typeActe.getMaxPages() > 0 && copy.getPageNumber() > typeActe.getMaxPages()) {
                copy.close();
                partie++;
                nomFichier = repertoireATraiter.getRepertoire().getCanonicalPath() + File.separatorChar +
                        typeActe.getNom() + "__" + typeFichierProduit.name() + "_partie_" +
                        partie + "_" + dateHeure + ".pdf";
                listeFichiers.put(nomFichier, new ArrayList<PageAModifier>());
                copy = new PdfSmartCopy(doc, new FileOutputStream(nomFichier));
                doc.open();
            }
            //on délègue l'écriture au courrier lui-même
            Courrier courrier = dicoCles.get((cle.toString()));
            if(courrier.isaImprimer()) {
                listeFichiers = courrier.ecrit(listeFichiers, repertoireATraiter, copy, nomFichier);
                nbPagesTraitees += courrier.getNbPages();
                jLabel.setText(String.format("Pages %s traitées %s : %d/%d",
                        typeActe.getNom(), typeFichierProduit.name(), nbPagesTraitees, nbPages));
            }
        }
        doc.close();
        return listeFichiers;
    }
}
