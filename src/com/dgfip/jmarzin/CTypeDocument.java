package com.dgfip.jmarzin;

import com.itextpdf.text.Rectangle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmarzin-cp on 13/05/2017.
 */
public class CTypeDocument {

    public Object getAdresseDest() {
        return adresseDest;
    }

    public void setAdresseDest(Object adresseDest) {
        this.adresseDest = adresseDest;
    }

    private Object adresseDest;

    public Object getAdresseExp() {
        return adresseExp;
    }

    public void setAdresseExp(Object adresseExp) {
        this.adresseExp = adresseExp;
    }

    private Object adresseExp;

    private static Map<String,CTypeDocument> dico = new HashMap<String, CTypeDocument>();

    public static Map<String, CTypeDocument> getDico() {
        return dico;
    }

    public static void setDico(Map<String, CTypeDocument> dico) {
        CTypeDocument.dico = dico;
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

    public void setInsereVerso(boolean insereVerso) {
        this.insereVerso = insereVerso;
    }

    private String nom;
    private TypeActe typeActe;
    private int rotation;
    private Rectangle rectExp;
    private boolean deleteExp;
    private Rectangle rectDest;
    private boolean deleteDest;
    private String chaineType;
    private String regexpCle;
    private String prefixeCle;
    private String chaineSousPlis;
    private String chaineService;
    private boolean plusieursPages;
    private boolean pageImpaire;

    public boolean isInsereVerso() {
        return insereVerso;
    }


    private boolean insereVerso;

    CTypeDocument(){
    }

    CTypeDocument(String nom,
                  TypeActe typeActe, int rotation, Rectangle rectExp,
                  boolean deleteExp, Rectangle rectDest, boolean deleteDest,
                  String chaineType, String regexpCle, String prefixeCle,
                  String chaineSousPlis, String chaineService,
                  boolean plusieursPages, boolean pageImpaire,
                  boolean insereVerso) {
        this.nom = nom;
        this.typeActe = typeActe;
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
        this.insereVerso = insereVerso;
        dico.put(nom, this);
    }

    static CTypeDocument get(String nom){
        assert (dico.containsKey(nom));
        return dico.get(nom);
    }

    static Collection<CTypeDocument> values() {
        return dico.values();
    }
}
