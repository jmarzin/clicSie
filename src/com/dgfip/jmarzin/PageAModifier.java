package com.dgfip.jmarzin;

/**
 * Cette classe représente une page à mettre aux normes.
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class PageAModifier {
    /**
     * répertoire d'origine de la page
     */
    private RepertoireATraiter repertoireATraiter;
    RepertoireATraiter getRepertoireATraiter() {
        return repertoireATraiter;
    }
    /**
     * rang de la page dans son document
     */
    private int ipage;
    int getIpage() {
        return ipage;
    }
    /**
     * indicateur de rupture. Vrai pour la première
     * page de chaque courrier.
     */
    private boolean rupture;
    boolean isRupture() { return rupture; }
    /**
     * type de document de la page
     */
    private TypeDocument typeDocument;
    TypeDocument getTypeDocument() {
        return typeDocument;
    }
    /**
     * crée une instance de page à modifier.
     * @param ipage rang de la page dans son document
     * @param typeDocument type de document de la page
     * @param rupture indicateur de début de courrier
     */
    PageAModifier (int ipage, TypeDocument typeDocument, boolean rupture, RepertoireATraiter repertoireATraiter) {
        this.ipage = ipage;
        this.typeDocument = typeDocument;
        this.rupture = rupture;
        this.repertoireATraiter = repertoireATraiter;
    }
}