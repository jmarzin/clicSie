package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cette classe représente un fichier pdf à traiter
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 20 mai 2017
 */
class FichierPdfATraiter {
    /**
     * Le répertoire où se trouve le fichier
     */
    private RepertoireATraiter repertoireATraiter;
    RepertoireATraiter getRepertoireATraiter() {
        return repertoireATraiter;
    }
    /**
     * Le lecteur Itext du fichier
     */
    private PdfReader lecteurPdf;
    PdfReader getLecteurPdf() { return lecteurPdf;}

    /**
     * Le type de document du fichier
     * @see TypeDocument
     */
    private TypeDocument typeFichier;
    TypeDocument getTypeFichier() { return typeFichier;}
    /**
     * Le texte contenu dans la page indiquée
     * du fichier pdf.
     *
     * @param page le rang de la page demandée
     * @return le texte contenu
     */
    String getChaine(int page) {
        String chaine = "";
        try {
            chaine = PdfTextExtractor.getTextFromPage(lecteurPdf, page);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chaine;
    }
    /**
     * Constructeur d'une instance de fichier
     * pdf à traiter
     * Le type de document qu'il contient est
     * identifié grâce à une chaîne de caractère
     * avec joker (regex)
     *
     * @param fichier le fichier à représenter
     */
    FichierPdfATraiter(RepertoireATraiter repertoireATraiter, File fichier) {
        try {
            this.lecteurPdf = new PdfReader(fichier.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String chaine = this.getChaine(1);
        this.typeFichier = null;
        this.repertoireATraiter = repertoireATraiter;
        for(TypeDocument typeDocument : TypeDocument.values()) {
            Pattern pattern = Pattern.compile(typeDocument.getChaineType(), Pattern.MULTILINE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(chaine);
            if(matcher.matches()) {
                this.typeFichier = typeDocument;
                break;
            }
        }
    }
}
