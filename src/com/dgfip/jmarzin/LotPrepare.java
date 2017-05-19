package com.dgfip.jmarzin;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.dgfip.jmarzin.ClicSie.jLabel;
import static com.dgfip.jmarzin.ClicSie.log;

/**
 * Created by jmarzin-cp on 12/05/2017.
 */
public class LotPrepare {
    private int nbPages = 0;
    private Map<TypeActe, LotParActe> dicoTypesActe = new HashMap<TypeActe, LotParActe>();

    LotPrepare() {
    }

    void ajout(DocumentAdm documentAdm) {
        if(documentAdm.getNbPages() == 0) return;
        if (dicoTypesActe.containsKey(documentAdm.getTypeActe())) {
            dicoTypesActe.get(documentAdm.getTypeActe()).ajout(documentAdm);
        } else {
            dicoTypesActe.put(documentAdm.getTypeActe(), new LotParActe(documentAdm));
        }
        nbPages += documentAdm.getNbPages();
    }

    void verif() {
        for (LotParActe lotParActe : dicoTypesActe.values()) {
            lotParActe.verif();
        }
    }

    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                           RepertoireATraiter repertoireATraiter,
                                           String dateHeure) throws IOException, DocumentException {
        for (LotParActe lotParActe : dicoTypesActe.values()) {
            listeFichiers = lotParActe.ecrit(listeFichiers, repertoireATraiter, dateHeure);
        }
        return listeFichiers;
    }

    void clicEsi(String nomFichier, int numeroMethode, List<PageAModifier> pages) throws IOException, DocumentException {
        if (numeroMethode == 1) {
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
        } else {
            Clicesiplus clic = new Clicesiplus(nomFichier);

            for (PageAModifier page : pages) {
                int i = page.getIpage();
                String[] texte1 = null;
                String[] texte2 = null;
                jLabel.setText(String.format("Fichier %s, pages converties : %d/%d",
                        nomFichier.substring(nomFichier.lastIndexOf('\\') + 1), i, clic.getNbTotalPages()));
                //récupérer l'adresse du SIE si nécessaire
                if (page.getTypeDocument().getRectExp() != null) {
                    texte1 = clic.getAdresse("Exp", page);
                    texte1[1] += " - recouvrement";
                }
                //récupérer l'adresse du destinataire si nécessaire
                if (page.getTypeDocument().getRectDest() != null) {
                    texte2 = clic.getAdresse("Dest", page);
                }
                //effacer l'adresse expéditeur si nécessaire
                if (page.getTypeDocument().isDeleteExp()) clic.deleteAdresse("Exp", page);
                //effacer l'adresse destinataire si nécessaire
                if (page.getTypeDocument().isDeleteDest()) clic.deleteAdresse("Dest", page);
                //replacer l'adresse SIE si nécessaire
                if (page.getTypeDocument().getRectExp() != null) clic.replaceAdresse("Exp", texte1, i);
                //replacer l'adresse destinataire
                if (page.getTypeDocument().getRectDest() != null) clic.replaceAdresse("Dest", texte2, i);
                //mettre les trois dièses
                clic.diese(i);
            }
            clic.close();
            File fichier = new File(nomFichier);
            if (!fichier.delete()) log(String.format("Suppression de %s impossible", fichier.getName()));
        }
    }
}
