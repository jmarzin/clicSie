package com.dgfip.jmarzin;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dgfip.jmarzin.ClicSie.log;

/**
 * Cette classe représente un courrier administratif de base.
 * Il peut contenir plusieurs documents relevant du même acte
 * et possédant la même valeur de clé
 * @author jmarzin-cp
 * @since 12/05/2017
 * @version 1.0
 */
class Courrier {
    /**
     * Nombre de pages du courrier
     */
    private int nbPages = 0;
    int getNbPages() {
        return nbPages;
    }
    /**
     * Type d'acte du courrier
     * @see TypeActe
     */
    private TypeActe typeActe;
    /**
     * Type de fichier produit (sous-plis ou service)
     * @see TypeFichierProduit
     */
    private TypeFichierProduit typeFichierProduit;
    /**
     * Clé d'identification unique du courrier
     */
    private String cle;
    /**
     * Dictionnaire des documents par type
     */
    private Map<TypeDocument, DocumentAdm> dicoTypesDocument = new HashMap<TypeDocument, DocumentAdm>();
    /**
     * Indicateur d'impression du courrier
     * mis à faux en cas d'erreur
     */
    private boolean aImprimer = true;
    boolean isaImprimer() {
        return aImprimer;
    }
    /**
     * Constructeur d'un courrier à partir
     * d'un document administratif
     * @param documentAdm 1er document administratif du courrier
     */
    Courrier(DocumentAdm documentAdm) {
        this.typeActe = documentAdm.getTypeActe();
        this.typeFichierProduit = documentAdm.getTypeFichierProduit();
        this.cle = documentAdm.getCle();
        ajout(documentAdm);
    }
    /**
     * Ajout d'un document administratif à un courrier
     * Mise à jour du dictionnaire des documents par type
     * et du nombre de pages
     * @param documentAdm le document administratif à ajouter
     */
    void ajout(DocumentAdm documentAdm) {
        if (dicoTypesDocument.containsKey(documentAdm.getTypeDocument())) {
            log(String.format("Nouveau courrier %s pour la cle %s. Il ne sera pas imprimé", documentAdm.getTypeDocument(), documentAdm.getCle()));
        } else {
            dicoTypesDocument.put(documentAdm.getTypeDocument(), documentAdm);
            nbPages += documentAdm.getNbPages();
        }
    }
    /**
     * Vérification de la cohérence métier d'un courrier
     * On peut vérifier l'absence d'une annexe, le fait qu'elle soit
     * orpheline du document principal...
     */
    void verif() {
        if (this.typeFichierProduit == TypeFichierProduit.SousPlis &&
                dicoTypesDocument.containsKey(TypeDocument.get("SIE_ATD")) &&
                !dicoTypesDocument.containsKey(TypeDocument.get("SIE_ATD_BULLETIN_REPONSE"))) {
            log("L'atd N° " + cle + " n'a pas de bulletin réponse ; il sera envoyé malgré tout.");
        }
        if (this.typeFichierProduit == TypeFichierProduit.SousPlis &&
                dicoTypesDocument.containsKey(TypeDocument.get("SIE_ATD_BULLETIN_REPONSE")) &&
                !dicoTypesDocument.containsKey(TypeDocument.get("SIE_ATD"))) {
            log("L'atd N° " + cle + " n'a qu'un bulletin réponse ; celui-ci ne sera pas envoyé.");
            this.aImprimer = false;
        }
        if (dicoTypesDocument.containsKey(TypeDocument.get("SIE_CVAE_2807")) &&
                !dicoTypesDocument.containsKey(TypeDocument.get("SIE_CVAE_RELANCE")) &&
                !dicoTypesDocument.containsKey(TypeDocument.get("SIE_CVAE_MAJ5")) &&
                !dicoTypesDocument.containsKey(TypeDocument.get("SIE_CVAE_MAJO02"))) {
            log("Le 2807 " + cle + " est isolé. Il ne sera pas imprimé");
            this.aImprimer = false;
        }
    }
    /**
     * Ecriture d'un courrier
     *
     * Les documents sont écrits dans l'ordre des types
     * de documents au sein d'un type d'acte.
     * Un verso est inséré s'il le faut si le type de
     * fichier est "sous-plis", et remplacé par une
     * page blanche pour l'exemplaire "service".
     * Les pages qui le doivent subissent une rotation.
     * Ajoute une page blanche si le document doit commencer
     * par une page impaire et si la page est paire.
     *
     * Les pages à modifier sont celles qui devront subir
     * une transformation pour mise au normes dans une
     * étape ultérieure, ainsi que la première page du
     * courrier qui doit recevoir la marque de changement
     * de courrier.
     *
     * @param listeFichiers dictionnaire des pages à modifier
     *                      par fichier écrit
     * @param repertoireATraiter répertoire contenant les fichiers
     *                           à traiter
     * @param copy Copy Itext représentant le fichier à écrire
     * @param nomFichier nom du fichier à écrire
     * @return dictionnaires des pages à modifier mis à jour des
     * nouvelles pages écrites
     * @throws DocumentException Pb d'écriture de page
     * @throws IOException Pb d'écriture d'une page lue
     */
    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                           RepertoireATraiter repertoireATraiter,
                                           PdfSmartCopy copy,
                                           String nomFichier) throws DocumentException, IOException {
        assert aImprimer;
        boolean rupture = true; // la première page d'un courrier doit être marquée ###
        TypeDocument[] typesDoc = typeActe.typeCourriersOrdonnes();
        for (TypeDocument typedoc : typesDoc) {
            PdfImportedPage versoPdf = null;
            // récupération du verso le cas échéant
            if (typeFichierProduit == TypeFichierProduit.SousPlis && typedoc.getVersoInsere() != null) {
                TypeDocument verso = TypeDocument.get(typedoc.getVersoInsere());
                PdfReader versoFichierPdf = repertoireATraiter.getVersoParNom(verso);
                versoPdf = copy.getImportedPage(versoFichierPdf, 1);
            }
            if (dicoTypesDocument.containsKey(typedoc)) {
                DocumentAdm documentAdm = dicoTypesDocument.get(typedoc);
                List<PageLue> pagesLues = documentAdm.getPages();
                for (int j = 0; j < pagesLues.size(); j++) {
                    PageLue pageLue = pagesLues.get(j);
                    //Récupère la rotation actuelle de la page pour tourner la page correctement
                    PdfDictionary pageP = pageLue.getLecteurPdf().getPageN(pageLue.getIpage());
                    PdfNumber rotate = pageP.getAsNumber(PdfName.ROTATE);
                    if (rotate == null) {
                        pageP.put(PdfName.ROTATE, new PdfNumber(typedoc.getRotation()));
                    } else {
                        pageP.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + typedoc.getRotation()) % 360));
                    }
                    PdfImportedPage pageOriginale = copy.getImportedPage(pageLue.getLecteurPdf(), pageLue.getIpage());
                    //Ajoute une page blanche si nécessaire
                    if (j == 0 && typedoc.isPageImpaire() && (copy.getPageNumber() % 2) == 0) {
                        copy.addPage(PageSize.A4, 0);
                    }
                    copy.addPage(pageOriginale);
                    //Identifie s'il faudra modifier la page en fonction du type de document
                    if (rupture || (j == 0 && (typedoc.getRectDest() != null || typedoc.getRectExp() != null ||
                            typedoc.getPlaceDate() != null || typedoc.getPlaceSignature() != null))) {
                        listeFichiers.get(nomFichier).add(new PageAModifier(copy.getPageNumber() - 1,
                                typedoc, rupture, documentAdm.getRepertoireATraiter()));
                    }
                    rupture = false; // les pages suivantes du courrier ne doivent pas être marquées ###
                    //Insère le verso si nécessaire
                    if (typedoc.getVersoInsere() != null) {
                        if (versoPdf == null) {
                            copy.addPage(PageSize.A4, 0);
                        } else {
                            copy.addPage(versoPdf);
                        }
                    }
                }
            }
        }
        return listeFichiers;
    }
}