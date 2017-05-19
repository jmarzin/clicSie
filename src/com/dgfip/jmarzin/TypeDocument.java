package com.dgfip.jmarzin;

import com.itextpdf.text.Rectangle;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmarzin-cp on 13/05/2017.
 */
public class TypeDocument {

    public Object getAdresseDest() {
        return adresseDest;
    }

    public void setAdresseDest(Object adresseDest) {
        this.adresseDest = adresseDest;
    }

    private Object adresseDest = null;

    public Object getPlaceDate() {
        return placeDate;
    }

    public void setPlaceDate(Object placeDate) {
        this.placeDate = placeDate;
    }

    private Object placeDate = null;


    public Object getAdresseExp() {
        return adresseExp;
    }

    public void setAdresseExp(Object adresseExp) {
        this.adresseExp = adresseExp;
    }

    private Object adresseExp = null;

    private static Map<String,TypeDocument> dico = new HashMap<String, TypeDocument>();

    public static Map<String, TypeDocument> getDico() {
        return dico;
    }

    public static void setDico(Map<String, TypeDocument> dico) {
        TypeDocument.dico = dico;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeActe getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(TypeActe typeActe) {
        this.typeActe = typeActe;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public Rectangle getRectExp() {
        return rectExp;
    }

    public void setRectExp(Rectangle rectExp) {
        this.rectExp = rectExp;
    }

    public boolean isDeleteExp() {
        return deleteExp;
    }

    public void setDeleteExp(boolean deleteExp) {
        this.deleteExp = deleteExp;
    }

    public Rectangle getRectDest() {
        return rectDest;
    }

    public void setRectDest(Rectangle rectDest) {
        this.rectDest = rectDest;
    }

    public boolean isDeleteDest() {
        return deleteDest;
    }

    public void setDeleteDest(boolean deleteDest) {
        this.deleteDest = deleteDest;
    }

    public String getChaineType() {
        return chaineType;
    }

    public void setChaineType(String chaineType) {
        this.chaineType = chaineType;
    }

    public String getRegexpCle() {
        return regexpCle;
    }

    public void setRegexpCle(String regexpCle) {
        this.regexpCle = regexpCle;
    }

    public String getPrefixeCle() {
        return prefixeCle;
    }

    public void setPrefixeCle(String prefixeCle) {
        this.prefixeCle = prefixeCle;
    }

    public String getChaineSousPlis() {
        return chaineSousPlis;
    }

    public void setChaineSousPlis(String chaineSousPlis) {
        this.chaineSousPlis = chaineSousPlis;
    }

    public String getChaineService() {
        return chaineService;
    }

    public void setChaineService(String chaineService) {
        this.chaineService = chaineService;
    }

    public boolean isPlusieursPages() {
        return plusieursPages;
    }

    public void setPlusieursPages(boolean plusieursPages) {
        this.plusieursPages = plusieursPages;
    }

    public boolean isPageImpaire() {
        return pageImpaire;
    }

    public void setPageImpaire(boolean pageImpaire) {
        this.pageImpaire = pageImpaire;
    }

    private String nom;
    private TypeActe typeActe;

    public String getNomTypeActe() {
        return nomTypeActe;
    }

    public void setNomTypeActe(String nomTypeActe) {
        this.nomTypeActe = nomTypeActe;
        this.typeActe = TypeActe.get(nomTypeActe);
    }

    private String nomTypeActe;
    public int getRangTypeActe() {
        return rangTypeActe;
    }

    public void setRangTypeActe(int rangTypeActe) {
        this.rangTypeActe = rangTypeActe;
    }

    private int rangTypeActe = 1;
    private int rotation = 0;
    private Rectangle rectExp;
    private boolean deleteExp = false;
    private Rectangle rectDest;
    private boolean deleteDest = false;
    private String chaineType;
    private String regexpCle;
    private String prefixeCle;
    private String chaineSousPlis = "";
    private String chaineService = null;
    private boolean plusieursPages = false;
    private boolean pageImpaire = true;


    public String getVersoInsere() {
        return versoInsere;
    }

    public void setVersoInsere(String versoInsere) {
        this.versoInsere = versoInsere;
    }

    private String versoInsere;

    TypeDocument(){
    }

    TypeDocument(String nom,
                 TypeActe typeActe, int rangTypeActe, int rotation, Rectangle rectExp,
                 boolean deleteExp, Rectangle rectDest, boolean deleteDest,
                 String chaineType, String regexpCle, String prefixeCle,
                 String chaineSousPlis, String chaineService,
                 boolean plusieursPages, boolean pageImpaire,
                 String versoInsere) {
        this.nom = nom;
        this.typeActe = typeActe;
        this.rangTypeActe = rangTypeActe;
        this.rotation = rotation;
        this.rectExp = rectExp;
        this.deleteExp = deleteExp;
        this.rectDest = rectDest;
        this.deleteDest = deleteDest;
        this.chaineType = chaineType;
        this.regexpCle = regexpCle;
        this.prefixeCle = prefixeCle;
        this.chaineSousPlis = chaineSousPlis;
        this.chaineService = chaineService;
        this.plusieursPages = plusieursPages;
        this.pageImpaire = pageImpaire;
        this.versoInsere = versoInsere;
        dico.put(nom, this);
    }

    static TypeDocument get(String nom){
        assert (dico.containsKey(nom));
        return dico.get(nom);
    }

    boolean isVerso() {
        for(TypeDocument type : values()) {
            if(type.getVersoInsere() != null && type.getVersoInsere().equals(nom)) {
                return true;
            }
        }
        return false;
    }
    static Collection<TypeDocument> values() {
        return dico.values();
    }
}
