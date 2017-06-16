package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Cette classe représente une page lue dans
 * un fichier pdf à traiter
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class PageLue {
    /**
     * Le lecteur Itext du fichier qui contient la page.
     */
    private PdfReader lecteurPdf;
    PdfReader getLecteurPdf() {
        return lecteurPdf;
    }
    /**
     * Le rang de la page dans le fichier. La 1ère page
     * est la page 1.
     */
    private int ipage;
    int getIpage() {
        return ipage;
    }
    /**
     * Crée une instance de page lue
     * @param fichierPdfATraiter le lecteur Itext du fichier pdf
     * @param ipage le rang de la page dans le fichier
     */
    PageLue(FichierPdfATraiter fichierPdfATraiter, int ipage) {
        this.lecteurPdf = fichierPdfATraiter.getLecteurPdf();
        this.ipage = ipage;
    }
}
