package com.dgfip.jmarzin;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.*;

/**
 * Cette classe représente un lot de courriers pour un
 * type d'acte administratif. Il contient un dictionnaire
 * des lots par type de fichier produit qu'il renferme
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class LotParActe {
    /**
     * Nombres de pages que le lot comporte
     */
    private int nbPages = 0;
    /**
     * Type d'acte du lot
     */
    private TypeActe typeActe;
    /**
     * dictionnaire des lots par type de fichier produit
     * qu'il renferme
     */
    private Map<TypeFichierProduit, FichierProduit> dicoTypesFichierProduit = new HashMap<TypeFichierProduit, FichierProduit>();
    /**
     * Construit un nouveau lot avec le premier document qu'il contient
     * @param documentAdm le premier document du lot
     */
    LotParActe(DocumentAdm documentAdm) {
        this.typeActe = documentAdm.getTypeActe();
        ajout(documentAdm);
    }
    /**
     * Ajoute un document au lot courant. Si le dictionnaire
     * des types produits contient déjà une entrée pour
     * le type de fichier du document, on ajoute le
     * document au lot fichier produit correspondant.
     * Sinon, on crée un nouveau lot fichier produit,
     * une nouvelle entrée au dictionnaire et on ajoute
     * le document au lot fichier produit.
     * On met à jour le nombre de pages du lot courant.
     *
     * @param documentAdm Le document à ajouter
     */
    void ajout(DocumentAdm documentAdm) {
        if (dicoTypesFichierProduit.containsKey(documentAdm.getTypeFichierProduit())) {
            dicoTypesFichierProduit.get(documentAdm.getTypeFichierProduit()).ajout(documentAdm);
        } else {
            dicoTypesFichierProduit.put(documentAdm.getTypeFichierProduit(), new FichierProduit(documentAdm));
        }
        nbPages += documentAdm.getNbPages();
    }
    /**
     * Vérifie le lot, par appel à la vérification
     * de chacun des lots fichiers produits qu'il contient
     */
    void verif() {
        for (FichierProduit fichierProduit : dicoTypesFichierProduit.values()) {
            fichierProduit.verif();
        }
    }
    /**
     * Ecrit le lot, par appel à l'écriture de chacun
     * les lots fichiers produits qu'il contient.
     *
     * @param listeFichiers liste des fichiers résultats avec
     *                      la liste des pages qui seront à
     *                      mettre aux normes clicesiplus
     * @param repertoireATraiter instance représentant le
     *                           répertoire des fichiers à traiter
     * @param dateHeure groupe date-heure pour les noms de fichiers
     * @return la liste des fichiers résultats à jour des nouveaux fichiers
     * @throws IOException Pb d'écriture de page
     * @throws DocumentException Pb d'écriture d'une page lue
     */
    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                           RepertoireATraiter repertoireATraiter,
                                           String dateHeure) throws IOException, DocumentException {
        for (FichierProduit fichierProduit : dicoTypesFichierProduit.values()) {
            listeFichiers = fichierProduit.ecrit(listeFichiers, repertoireATraiter, dateHeure);
        }
        return listeFichiers;
    }
}

