package com.dgfip.jmarzin;

/**
 * Created by jmarzin-cp on 08/05/2017.
 */
class PageAModifier {
    private int ipage;
    int getIpage() {
        return ipage;
    }

    private boolean rupture;
    boolean isRupture() { return rupture; }

    private TypeDocument typeDocument;
    TypeDocument getTypeDocument() {
        return typeDocument;
    }

    PageAModifier (int ipage, TypeDocument typeDocument, boolean rupture) {
        this.ipage = ipage;
        this.typeDocument = typeDocument;
        this.rupture = rupture;
    }
}