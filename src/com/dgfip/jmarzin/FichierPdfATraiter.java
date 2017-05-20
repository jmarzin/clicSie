package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FichierPdfATraiter {

    private PdfReader lecteurPdf;
    PdfReader getLecteurPdf() { return lecteurPdf;}

    private TypeDocument typeFichier;
    TypeDocument getTypeFichier() { return typeFichier;}

    String getChaine(int page) {
        String chaine = "";
        try {
            chaine = PdfTextExtractor.getTextFromPage(lecteurPdf, page);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chaine;
    }

    FichierPdfATraiter(File fichier) {
        try {
            this.lecteurPdf = new PdfReader(fichier.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String chaine = this.getChaine(1);
        this.typeFichier = null;
        for(TypeDocument typeDocument : TypeDocument.values()) {
            Pattern pattern = Pattern.compile(typeDocument.getChaineType(), Pattern.MULTILINE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(chaine);
            if(matcher.matches()) {
                this.typeFichier = typeDocument;
                //if(typeFichier.getTypeActe() != null) ClicSie.addEnsembleEvenements(typeFichier.getTypeActe());
                break;
            }
        }
    }
}
