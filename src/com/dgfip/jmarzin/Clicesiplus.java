package com.dgfip.jmarzin;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.text.pdf.pdfcleanup.PdfCleanUpProcessor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Cette classe représente le fichier Clicesiplus généré
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 20 mai 2017
 */
class Clicesiplus {
    /**
     * Fonte arial 4 utilisée pour marquer le document comme issu
     * de cette application
     */
    private Font arial4;
    /**
     * Fonte arial 6 utilisée pour le marquage des pages de
     * nouveau courrier (###)
     */
    private Font arial6;
    /**
     * Fonte arial 8 utilisée pour l'adresse du service
     * expéditeur
     */
    private Font arial8;
    /**
     * Fonte arial 10 utilisée pour la date et la signature
     */
    private Font arial10;
    /**
     * Font OCR-BT 10 utilisée pour l'adresse du destinataire
     */
    private Font ocr10;
    /**
     * Liste des zones à effacer sur le document
     */
    private List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
    /**
     * Stamper Itext utilisé comme document résultat
     * de la mise aux normes ClicEsiPlus
     */
    private PdfStamper stamper;
    /**
     * Nom du fichier résultat de la mise aux
     * normes ClicEsiPlus
     */
    private String nomFichierProduit;
    String getNomFichierProduit() {
        return nomFichierProduit;
    }
    /**
     * Lecteur Itext du fichier à mettre aux normes
     * ClicEsiPlus
     */
    private PdfReader lecteurPdf;
    /**
     * Nombre total de pages du fichier à mettre
     * aux normes ClicEsiPlus
     */
    private int nbTotalPages;
    int getNbTotalPages() {
        return nbTotalPages;
    }
    /**
     * Tableau à double entrée permettant de supprimer
     * les accents de l'adresse du destinataire, mal
     * traduits sur le document final en OCR-BT
     */
    private static final char[][] accents = {{'à','a'},{'é','e'},{'è','e'},{'ê','e'},{'ë','e'},
            {'ï','i'},{'ô','o'},{'ù','u'},{'.',' '}};
    /**
     * Construit une instance clicesiplus
     * Il initialise les fontes, initialise le lecteur
     * du fichier à mettre aux normes, le nom du fichier
     * résultat aux normes et le stamper associé
     * @param nomFichier Nom du fichier à mettre aux normes
     * @throws IOException Mauvaise lecture des fichiers de fontes
     * @throws DocumentException problème à la création du stamper
     */
    Clicesiplus(String nomFichier) throws IOException, DocumentException {
        BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        this.arial4 = new Font(bf,4);
        this.arial6 = new Font(bf, 6);
        this.arial8 = new Font(bf,8);
        this.arial10 = new Font(bf,10);
        bf = BaseFont.createFont("C:\\Windows\\Fonts\\OCR-B10BT.TTF", BaseFont.WINANSI, BaseFont.EMBEDDED);
        this.ocr10 = new Font(bf, 10);
        this.lecteurPdf = new PdfReader(nomFichier);
        this.nbTotalPages = lecteurPdf.getNumberOfPages();
        this.nomFichierProduit = nomFichier.replaceAll(".pdf$", "_ClicEsi.pdf");
        this.stamper = new PdfStamper(lecteurPdf, new FileOutputStream(nomFichierProduit));
        marqueFichier();
    }
    /**
     * Récupère une adresse, celle du destinataire
     * ou celle du service expéditeur en fontion du
     * paramètre. Le rectangle à scruter est
     * fourni par le type de document de la page.
     * @param typeAdresse type de l'adresse à récupèrer
     * @param page rang de la page où elle se trouve
     * @return l'adresse récupérée
     * @throws IOException Problème à l'exctraction du texte
     */
    String[] getAdresse(TypeAdresse typeAdresse, PageAModifier page) throws IOException {
        Rectangle rect = (typeAdresse == TypeAdresse.Exp) ? page.getTypeDocument().getRectExp() : page.getTypeDocument().getRectDest();
        RegionTextRenderFilter filter = new RegionTextRenderFilter(rect);
        FilteredTextRenderListener strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
        return PdfTextExtractor.getTextFromPage(lecteurPdf, page.getIpage(), strategy).split("\n");
    }
    /**
     * Efface une adresse, celle du destinataire
     * ou celle du service expéditeur en fonction du
     * paramètre. Le rectangle à effacer est fourni
     * par le type de document de la page.
     * @param typeAdresse type de l'adresse à effacer
     * @param page rang de la page où elle se trouve
     * @throws IOException problème au moment de la suppression
     * @throws DocumentException problème au moment de la suppression
     */
    void deleteAdresse(TypeAdresse typeAdresse, PageAModifier page) throws IOException, DocumentException {
        Rectangle rect = (typeAdresse == TypeAdresse.Exp) ? page.getTypeDocument().getRectExp() : page.getTypeDocument().getRectDest();
        cleanUpLocations.add(new PdfCleanUpLocation(page.getIpage(),rect));
        PdfCleanUpProcessor cleaner = new PdfCleanUpProcessor(cleanUpLocations,stamper);
        cleaner.cleanUp();
        cleanUpLocations.clear();
    }
    /**
     * Replace l'adresse fournie à l'endroit déterminé
     * par son type.
     * Les lignes d'adresse expéditeur commençant
     * par 'CS ' sont supprimées
     * @param typeAdresse el type de l'adresse à replacer
     * @param texte les lignes de l'adresse
     * @param ipage rang de la page où elle doit être replacée
     */
    void replaceAdresse(TypeAdresse typeAdresse, String[] texte, int ipage) {
        Float y;
        Float espace;
        Font fonte;

        if(typeAdresse == TypeAdresse.Exp) {
            fonte = arial8;
            y = 730f;
            espace = 10f;
        } else {
            fonte = ocr10;
            texte = enleveAccent(texte);
            y = 650f;
            espace = 12f;
        }
        PdfContentByte canvas = stamper.getOverContent(ipage);
        for (String ligne: texte) {
            if(!ligne.startsWith("CS ")) {
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                        new Phrase(ligne, fonte),300f, y, 0);
                y -= espace;
            }
        }
    }

    /**
     * Place la date du jour sur la page indiquée
     * @param placeDate place de la date sur la page
     * @param ipage rang de la page où il faut la placer
     */
    void placeDate(Map<String,Float> placeDate, int ipage) {
        PdfContentByte canvas = stamper.getOverContent(ipage);
        Date now = Calendar.getInstance().getTime();
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(new SimpleDateFormat("dd/MM/yyyy").format(now),arial10), placeDate.get("x"), placeDate.get("y"),0);
    }
    /**
     * Marque le fichier comme ayant été traité par cette
     * fonction en plaçant un caractère '.' en haut à gauche
     * de la première page
     */
    private void marqueFichier() {
        PdfContentByte canvas = stamper.getUnderContent(1);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(".",arial4),5f,837f,0);
    }
    /**
     * Marque la page fournie avec '###' en bas et à gauche
     * @param ipage le rang de la page à marquer
     */
    void diese(int ipage) {
        PdfContentByte canvas = stamper.getOverContent(ipage);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase("###", arial6),28.4f, 28.76f, 0);
    }
    /**
     * Place la signature (une ligne grade,
     * une ligne nom prénom) fournie à la place indiquée
     * sur la page. Si le type de document de la page
     * indique de mettre le grade du signature, on place
     * les deux lignes, sinon, on place les nom prénom
     * seulement
     * @param placeSignature place de la 1ère ligne
     * @param isAvecGrade vrai s'il faut mettre le grade
     * @param signature tableau des lignes de la signature
     * @param ipage rang de la page où il faut la placer
     */
    void placeSignature(Map<String,Float> placeSignature, boolean isAvecGrade, String[] signature, int ipage) {
        int idep = 1;
        if(isAvecGrade) {
            idep = 0;
        }
        int inc = 0;
        PdfContentByte canvas = stamper.getOverContent(ipage);
        for(int i=idep; i < signature.length; i++) {
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase(signature[i], arial10), placeSignature.get("x"), placeSignature.get("y") + inc, 0);
            inc -= 11;
        }
    }
    /**
     * Ferme le lecteur du fichier d'origine
     * et le Stamper associé au fichier résultat
     * @throws IOException Pb à la fermeture du Stamper
     * @throws DocumentException Pb à la fermeture du Stamper
     */
    void close() throws IOException, DocumentException {
        stamper.close();
        lecteurPdf.close();
    }
    /**
     * Remplace les lettres accentuée d'une chaîne de
     * caractères par son équivalent non accentuée.
     * @param texte la chaîne de caractère à traiter
     * @return la nouvelle chaîne sans accents
     */
    private String[] enleveAccent(String[] texte) {
        for(int i = 0; i< texte.length; i++) {
            if(texte[i].contains("é")){
                texte[i] = texte[i];
            }
            for(char[] lettre: accents){
                texte[i] = texte[i].replace(lettre[0],lettre[1]);
            }
        }
        return texte;
    }
}
