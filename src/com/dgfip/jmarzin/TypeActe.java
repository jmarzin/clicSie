package com.dgfip.jmarzin;

import java.util.*;

/**
 * Created by jmarzin-cp on 16/05/2017.
 */
public class TypeActe {

    private String nom;
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    private boolean utiliseLO = false;
    public boolean isUtiliseLO() {
        return utiliseLO;
    }
    public void setUtiliseLO(boolean utiliseLO) {
        this.utiliseLO = utiliseLO;
    }

    private int maxPages = 0;
    public int getMaxPages() {
        return maxPages;
    }
    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    //public String[] getListeNomsTypesDocument() {
    //    return listeNomsTypesDocument;
    //}

    //public void setListeNomsTypesDocument(String[] listeNomsTypesDocument) {
    //    this.listeNomsTypesDocument = listeNomsTypesDocument;
    //}

    //public List<TypeDocument> getTypesOrdonnes() {
    //    return typesOrdonnes;
    //}

    //public void setTypesOrdonnes(List<TypeDocument> typesOrdonnes) {
    //    this.typesOrdonnes = typesOrdonnes;
    //}





    public static Map<String, TypeActe> getDico() {
        return dico;
    }

    public static void setDico(Map<String, TypeActe> dico) {
        TypeActe.dico = dico;
    }

    //private String[] listeNomsTypesDocument;
    //private List<TypeDocument> typesOrdonnes = new ArrayList<TypeDocument>();

    private static Map<String,TypeActe> dico = new HashMap<String, TypeActe>();

    TypeActe(){
    }

    //TypeActe(String nom){
    //  this.nom = nom;
    //  dico.put(nom, this);
    //}

    static Collection<TypeActe> values() {
        return dico.values();
    }

    static TypeActe get(String nom){
        assert (dico.containsKey(nom));
        return dico.get(nom);
    }

    TypeDocument[] typeCourriersOrdonnes() {
        Map<Integer, TypeDocument> dicoTypesParActe = new HashMap<Integer, TypeDocument>();
        for(String entree : TypeDocument.getDico().keySet()) {
            if (TypeDocument.getDico().get(entree).getTypeActe() == this) {
                dicoTypesParActe.put(TypeDocument.getDico().get(entree).getRangTypeActe(),TypeDocument.getDico().get(entree));
            }
        }
        Object[] cles = dicoTypesParActe.keySet().toArray();
        Arrays.sort(cles);
        List<TypeDocument> typeDocumentList = new ArrayList<TypeDocument>();
        for(Object cle : cles) {
            typeDocumentList.add(dicoTypesParActe.get(cle));
        }
        TypeDocument[] typeDocs= new TypeDocument[dicoTypesParActe.size()];
        return typeDocumentList.toArray(typeDocs);
    }
}
