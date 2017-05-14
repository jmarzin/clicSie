package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;

class PageLue {

    private PdfReader lecteurPdf;
    PdfReader getLecteurPdf() {
        return lecteurPdf;
    }

    private int ipage;
    int getIpage() {
        return ipage;
    }

    PageLue(FichierPdfATraiter fichierPdfATraiter, int ipage) {
        this.lecteurPdf = fichierPdfATraiter.getLecteurPdf();
        this.ipage = ipage;
    }
}
