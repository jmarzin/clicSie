package com.dgfip.jmarzin;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classeContenus représente un document administratif de base.
 *
 * @author jmarzin-cp
 * @since 12/05/2017
 * @version 1.0
 */
class DocumentAdm {

    /**
     * Le type de fichier produit auquel le document appartient
     */
    private TypeFichierProduit typeFichierProduit = null;

    /**
     * Modifie le type de fichier produit auquel le document appartient
     * @param typeFichierProduit nouveau type de fichier produit
     */
    void setTypeFichierProduit(TypeFichierProduit typeFichierProduit) {
        this.typeFichierProduit = typeFichierProduit;
    }

    /**
     *
     * @return le type fichier auquel le document appartient
     */
    TypeFichierProduit getTypeFichierProduit() {
        return typeFichierProduit;
    }


    /**
     * La cle d'unicité du destinataire
     */
    private String cle = "";

    /**
     * modifie la cle du document
     * @param cle le nouvelle cle
     */
    void setCle(String cle) {
        this.cle = cle;
    }

    /**
     *
     * @return la cle d'unicite du destinataire
     */
    String getCle() {
        return cle;
    }

    /**
     * Le type d'acte qui génère le document
     */
    private TypeActe typeActe = null;
    TypeActe getTypeActe() { return typeActe;}

    /**
     * Le type du document admnistratif
     * @see TypeDocument , #getTypeDocument()
     */
    private TypeDocument typeDocument = null;

    /**
     * Met à jour le type de document, et
     * le type d'acte qui le génère
     * @param typeDocument nouveau type du document
     */
    void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
        this.typeActe = typeDocument.getTypeActe();
    }

    /**
     *
     * @return le type du document
     */
    TypeDocument getTypeDocument() {
        return typeDocument;
    }

    /**
     * La liste des pages du documents
     * @see PageLue, #getPages()
     */
    private List<PageLue> pages;

    /**
     *
     * @return la liste des pages du document
     */
    List<PageLue> getPages() {
        return pages;
    }

    /**
     * Nombre de pages du document
     * @see #getNbPages()
     */
    private int nbPages = 0;

    /**
     *
     * @return le nombre de pages du document
     */
    int getNbPages() {
        return nbPages;
    }

    /**
     * ajoute une page à la fin du document
     * et met à jour le nombre de pages
     * @param pageLue page lue
     */
    void ajout(PageLue pageLue) {
        pages.add(pageLue);
        nbPages++;
    }

    /**
     * Crée un document vide du type fourni
     * @param typeDocument type du document
     */
    DocumentAdm (TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
        this.typeActe = typeDocument.getTypeActe();
        this.pages = new ArrayList<PageLue>();
        this.nbPages = 0;
    }

    /**
     * Crée un document du type fourni avec une première page
     * @param typeDocument type de document
     * @param pageLue page lue
     */
    DocumentAdm (TypeDocument typeDocument, PageLue pageLue) {
        this(typeDocument);
        ajout(pageLue);
    }
    /**
     * Crée un document vide de type nul
     */
    DocumentAdm() {
    }
}
