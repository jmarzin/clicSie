package com.dgfip.jmarzin;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe représente un document administratif de base.
 *
 * @author jmarzin-cp
 * @since 12/05/2017
 * @version 1.0
 */
class DocumentAdm {
    /**
     * Le répertoire où a été trouvé le document
     */
    private RepertoireATraiter repertoireATraiter;
    RepertoireATraiter getRepertoireATraiter() {
        return repertoireATraiter;
    }
    /**
     * Le type de fichier produit auquel le document appartient
     * @see TypeFichierProduit
     */
    private TypeFichierProduit typeFichierProduit = null;
    void setTypeFichierProduit(TypeFichierProduit typeFichierProduit) {
        this.typeFichierProduit = typeFichierProduit;
    }
    TypeFichierProduit getTypeFichierProduit() {
        return typeFichierProduit;
    }
    /**
     * La cle d'unicité du courrier qui contiendra
     * le document. Ce peut être un identifiant du
     * destinatire, ou de la procédure qui lui est
     * appliquée.
     */
    private String cle = "";
    void setCle(String cle) {
        this.cle = cle;
    }
    String getCle() {
        return cle;
    }
    /**
     * Le type d'acte qui est à l'origine du document
     */
    private TypeActe typeActe = null;
    TypeActe getTypeActe() { return typeActe;}
    /**
     * Le type du document admnistratif
     * @see TypeDocument , #getTypeDocument()
     */
    private TypeDocument typeDocument = null;
    TypeDocument getTypeDocument() {
        return typeDocument;
    }
    /**
     * La liste des pages du documents
     * @see PageLue
     */
    private List<PageLue> pages;
    List<PageLue> getPages() {
        return pages;
    }
    /**
     * Nombre de pages du document
     * @see #getNbPages()
     */
    private int nbPages = 0;
    int getNbPages() {
        return nbPages;
    }
    /**
     * Ajoute une page à un document existant
     * Incrément le nombre de pages.
     *
     * @param pageLue la page à ajouter
     */
    void ajout(PageLue pageLue) {
        pages.add(pageLue);
        nbPages++;
    }
    /**
     * Constructeur d'un document vide du type fourni
     * @param typeDocument type du document
     */
    DocumentAdm (RepertoireATraiter repertoireATraiter, TypeDocument typeDocument) {
        this.repertoireATraiter = repertoireATraiter;
        this.typeDocument = typeDocument;
        this.typeActe = typeDocument.getTypeActe();
        this.pages = new ArrayList<PageLue>();
        this.nbPages = 0;
    }
}
