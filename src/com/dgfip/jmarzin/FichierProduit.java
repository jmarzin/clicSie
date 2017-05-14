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
 * Created by jmarzin-cp on 12/05/2017. jma
 */
class FichierProduit {

    private int nbPages = 0;
    private TypeActe typeActe;
    private TypeFichierProduit typeFichierProduit;
    private Map<String, Courrier> dicoCles = new HashMap<String, Courrier>();

    FichierProduit(DocumentAdm documentAdm) {
        this.typeActe = documentAdm.getTypeActe();
        this.typeFichierProduit = documentAdm.getTypeFichierProduit();
        ajout(documentAdm);
    }

    void ajout(DocumentAdm documentAdm) {
        if (dicoCles.containsKey(documentAdm.getCle())) {
            dicoCles.get(documentAdm.getCle()).ajout(documentAdm);
        } else {
            dicoCles.put(documentAdm.getCle(), new Courrier(documentAdm));
        }
        nbPages += documentAdm.getNbPages();
    }

    void verif() {
        for (Courrier courrier : dicoCles.values()) {
            courrier.verif();
        }
    }

    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                                  int MAX_PAGES, File repertoire,
                                                  PdfReader verso,
                                                  String dateHeure) throws IOException, DocumentException {

        Document doc = new Document();
        PdfSmartCopy copy;
        PdfImportedPage versoPdf = null;
        String nomFichier;
        int nbPagesTraitees = 0;
        if (MAX_PAGES == 0 || nbPages <= MAX_PAGES) {
            nomFichier = repertoire.getCanonicalPath() + File.separatorChar +
                    typeActe.name() + "__" + typeFichierProduit.name() + "_" + dateHeure + ".pdf";
        } else {
            nomFichier = repertoire.getCanonicalPath() + File.separatorChar +
                    typeActe.name() + "__" + typeFichierProduit.name() + "_partie_1_" + dateHeure + ".pdf";
        }
        listeFichiers.put(nomFichier, new ArrayList<PageAModifier>());
        copy = new PdfSmartCopy(doc, new FileOutputStream(nomFichier));
        doc.open();
        if (typeFichierProduit == TypeFichierProduit.SousPlis) {
            versoPdf = copy.getImportedPage(verso, 1);
        }
        int partie = 1;
        Object[] clesTriees = dicoCles.keySet().toArray();
        Arrays.sort(clesTriees);
        for (Object cle : clesTriees) {
            if (MAX_PAGES > 0 && copy.getPageNumber() > MAX_PAGES) {
                copy.close();
                partie++;
                nomFichier = repertoire.getCanonicalPath() + File.separatorChar +
                        typeActe.name() + "__" + typeFichierProduit.name() + "_partie_" +
                        partie + "_" + dateHeure + ".pdf";
                listeFichiers.put(nomFichier, new ArrayList<PageAModifier>());
                copy = new PdfSmartCopy(doc, new FileOutputStream(nomFichier));
                doc.open();
                if (typeFichierProduit == TypeFichierProduit.SousPlis) {
                    versoPdf = copy.getImportedPage(verso, 1);
                }
            }
            Courrier courrier = dicoCles.get((cle.toString()));
            if(courrier.isaImprimer()) {
                listeFichiers = courrier.ecrit(listeFichiers, versoPdf, copy, nomFichier);
                nbPagesTraitees += courrier.getNbPages();
                jLabel.setText(String.format("Pages %s traitées %s : %d/%d",
                        typeActe.name(), typeFichierProduit.name(), nbPagesTraitees, nbPages));
            }
        }
        doc.close();
        return listeFichiers;
    }
}
