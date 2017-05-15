package com.dgfip.jmarzin;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by jmarzin-cp on 12/05/2017.
 */
public class LotParActe {

    private int nbPages = 0;
    private TypeActe typeActe;
    private Map<TypeFichierProduit, FichierProduit> dicoTypesFichierProduit = new HashMap<TypeFichierProduit, FichierProduit>();

    LotParActe(DocumentAdm documentAdm) {
        this.typeActe = documentAdm.getTypeActe();
        ajout(documentAdm);
    }

    void ajout(DocumentAdm documentAdm) {
        if (dicoTypesFichierProduit.containsKey(documentAdm.getTypeFichierProduit())) {
            dicoTypesFichierProduit.get(documentAdm.getTypeFichierProduit()).ajout(documentAdm);
        } else {
            dicoTypesFichierProduit.put(documentAdm.getTypeFichierProduit(), new FichierProduit(documentAdm));
        }
        nbPages += documentAdm.getNbPages();
    }

    void verif() {
        for (FichierProduit fichierProduit : dicoTypesFichierProduit.values()) {
            fichierProduit.verif();
        }
    }

    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                           int MAX_PAGES, RepertoireATraiter repertoireATraiter,
                                           String dateHeure) throws IOException, DocumentException {
        for (FichierProduit fichierProduit : dicoTypesFichierProduit.values()) {
            listeFichiers = fichierProduit.ecrit(listeFichiers, MAX_PAGES, repertoireATraiter, dateHeure);
        }
        return listeFichiers;
    }
}

