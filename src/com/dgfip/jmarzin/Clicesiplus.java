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

class Clicesiplus {
    private Font arial4;
    private Font arial6;
    private Font arial8;
    private Font arial10;
    private Font ocr10;
    private List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
    private PdfStamper stamper;

    public String getNomFichierProduit() {
        return nomFichierProduit;
    }

    private String nomFichierProduit;

    public PdfReader getLecteurPdf() {
        return lecteurPdf;
    }

    private PdfReader lecteurPdf;

    private int nbTotalPages;
    int getNbTotalPages() {
        return nbTotalPages;
    }

    private static final char[][] accents = {{'à','a'},{'é','e'},{'è','e'},{'ê','e'},{'ë','e'},
            {'ï','i'},{'ô','o'},{'ù','u'},{'.',' '}};

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

    String[] getAdresse(String adresse, PageAModifier page) throws IOException {
        Rectangle rect = (adresse.equals("Exp")) ? page.getTypeDocument().getRectExp() : page.getTypeDocument().getRectDest();
        RegionTextRenderFilter filter = new RegionTextRenderFilter(rect);
        FilteredTextRenderListener strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
        return PdfTextExtractor.getTextFromPage(lecteurPdf, page.getIpage(), strategy).split("\n");
    }

    void deleteAdresse(String adresse, PageAModifier page) throws IOException, DocumentException {
        Rectangle rect = (adresse.equals("Exp")) ? page.getTypeDocument().getRectExp() : page.getTypeDocument().getRectDest();
        cleanUpLocations.add(new PdfCleanUpLocation(page.getIpage(),rect));
        PdfCleanUpProcessor cleaner = new PdfCleanUpProcessor(cleanUpLocations,stamper);
        cleaner.cleanUp();
        cleanUpLocations.clear();
    }

    void replaceAdresse(String adresse, String[] texte, int ipage) {
        Float y;
        Float espace;
        Font fonte;

        if(adresse.equals("Exp")) {
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

    void placeDate(Map<String,Float> placeDate, int ipage) {
        PdfContentByte canvas = stamper.getOverContent(ipage);
        Date now = Calendar.getInstance().getTime();
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(new SimpleDateFormat("dd/MM/yyyy").format(now),arial10), placeDate.get("x"), placeDate.get("y"),0);
    }

    private void marqueFichier() {
        PdfContentByte canvas = stamper.getUnderContent(1);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(".",arial4),5f,837f,0);
    }

    void diese(int ipage) {
        PdfContentByte canvas = stamper.getOverContent(ipage);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase("###", arial6),28.4f, 28.76f, 0);
    }

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

    void close() throws IOException, DocumentException {
        stamper.close();
        lecteurPdf.close();
    }
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
