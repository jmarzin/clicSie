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

    private CTypeDocument typeDocument;
    CTypeDocument getTypeDocument() {
        return typeDocument;
    }

    PageAModifier (int ipage, CTypeDocument typeDocument, boolean rupture) {
        this.ipage = ipage;
        this.typeDocument = typeDocument;
        this.rupture = rupture;
    }
}