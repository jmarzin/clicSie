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
 * Cette classeContenus représente un courrier administratif de base.
 *
 * Il peut contenir plusieurs documents relevant du même acte
 * et possédant la même valeur de clé
 *
 * @author jmarzin-cp
 * @since 12/05/2017
 * @version 1.0
 */
class Courrier {

    private int nbPages = 0;
    int getNbPages() {
        return nbPages;
    }

    private TypeActe typeActe;
    private TypeFichierProduit typeFichierProduit;
    private String cle;
    private Map<TypeDocument, DocumentAdm> dicoTypesDocument = new HashMap<TypeDocument, DocumentAdm>();

    /**
     * Indicateur d'impression du courrier
     * mis à faux en cas d'erreur
     */
    private boolean aImprimer = true;
    boolean isaImprimer() {
        return aImprimer;
    }

    Courrier(DocumentAdm documentAdm) {
        this.typeActe = documentAdm.getTypeActe();
        this.typeFichierProduit = documentAdm.getTypeFichierProduit();
        this.cle = documentAdm.getCle();
        ajout(documentAdm);
    }

    void ajout(DocumentAdm documentAdm) {
        if (dicoTypesDocument.containsKey(documentAdm.getTypeDocument())) {
            log(String.format("Nouveau courrier %s pour la cle %s. Il ne sera pas imprimé", documentAdm.getTypeDocument(), documentAdm.getCle()));
        } else {
            dicoTypesDocument.put(documentAdm.getTypeDocument(), documentAdm);
            nbPages += documentAdm.getNbPages();
        }
    }

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

    Map<String, List<PageAModifier>> ecrit(Map<String, List<PageAModifier>> listeFichiers,
                                           RepertoireATraiter repertoireATraiter,
                                           PdfSmartCopy copy,
                                           String nomFichier) throws DocumentException, IOException {
        assert aImprimer;
        boolean rupture = true;
        TypeDocument[] typesDoc = typeActe.typeCourriersOrdonnes();
        for (int i = 0; i < typesDoc.length; i++) {
            TypeDocument typedoc = typesDoc[i];
            PdfImportedPage versoPdf = null;
            if(typeFichierProduit == TypeFichierProduit.SousPlis && typedoc.getVersoInsere() != null) {
                TypeDocument verso = TypeDocument.get(typedoc.getVersoInsere());
                PdfReader versoFichierPdf = repertoireATraiter.getVersoParNom(verso);
                versoPdf = copy.getImportedPage(versoFichierPdf,1);
            }
            if (dicoTypesDocument.containsKey(typedoc)) {
                DocumentAdm documentAdm = (DocumentAdm) dicoTypesDocument.get(typedoc);
                List<PageLue> pagesLues = documentAdm.getPages();

                for (int j = 0; j < pagesLues.size(); j++) { //PageLue pageLue: courrier.get(typedoc)) {
                    PageLue pageLue = pagesLues.get(j);
                    PdfDictionary pageP = pageLue.getLecteurPdf().getPageN(pageLue.getIpage());
                    PdfNumber rotate = pageP.getAsNumber(PdfName.ROTATE);
                    if (rotate == null) pageP.put(PdfName.ROTATE, new PdfNumber(typedoc.getRotation()));
                    else
                        pageP.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + typedoc.getRotation()) % 360));
                    PdfImportedPage pageOriginale = copy.getImportedPage(pageLue.getLecteurPdf(), pageLue.getIpage());
                    if (j == 0 && typedoc.isPageImpaire() && (copy.getPageNumber() % 2) == 0)
                        copy.addPage(PageSize.A4, 0);
                    copy.addPage(pageOriginale);
                    if(rupture || (j == 0 && (typedoc.getRectDest() != null || typedoc.getRectExp() != null ||
                            typedoc.getPlaceDate() != null || typedoc.getPlaceSignature() != null))) {
                        listeFichiers.get(nomFichier).add(new PageAModifier(copy.getPageNumber() - 1, typedoc, rupture));
                    }
                    rupture = false;
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