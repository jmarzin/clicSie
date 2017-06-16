package com.dgfip.jmarzin;

import com.itextpdf.text.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cette classe représente un type de document.
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 20 mai 2017
 */
public class TypeDocument {
    /**
     * le nom du type de document, obligatoire.
     * Par convention, ce nom pourra commencer
     * par le nom du type d'acte auquel il
     * participe, suivi éventuellement d'un '_'
     * et d'une désignation complémentaire, pour
     * en garantir l'unicité pour un type
     * d'acte, le tout en majuscules.
     */
    private String nom;
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
        dico.put(nom, this);
    }
    /**
     * l'expression régulière obligatoire permettant de
     * reconnaître les documents de ce type.
     * L'expression doit couvrir l'intégralité du texte
     * de la première page du document, par exemple
     * en commençant et en terminant par '.*' .
     */
    private String chaineType;
    String getChaineType() {
        return chaineType;
    }
    public void setChaineType(String chaineType) {
        this.chaineType = chaineType;
    }
    /**
     * le nom du type d'acte auquel participe
     * un document de ce type, obligatoire.
     */
    private String nomTypeActe;
    String getNomTypeActe() {
        return nomTypeActe;
    }
    public void setNomTypeActe(String nomTypeActe) {
        this.nomTypeActe = nomTypeActe;
        this.typeActe = TypeActe.get(nomTypeActe);
    }
    /**
     * le type d'acte auquel participe un document
     * de ce type. <b>Cette donnée n'est pas fournie
     * comme paramètre</b>, mais est déduite à
     * partir du nom du type d'acte.
     * @see #nomTypeActe
     */
    private TypeActe typeActe;
    TypeActe getTypeActe() {
        return typeActe;
    }
    public void setTypeActe(TypeActe typeActe) {
        this.typeActe = typeActe;
    }
    /**
     * le rang des documents de ce type au sein des documents
     * de l'acte. La valeur par défaut est 1. Quand un acte
     * comprend plusieurs types de documents, les numéros
     * doivent être différents et l'ordre fourni sera respecté
     * pour constituer les fichiers. Les documents qui doivent
     * être insérés dans le même courrier le seront en suivant
     * l'ordre prescrit.
     */
    private int rangTypeActe = 1;
    int getRangTypeActe() {
        return rangTypeActe;
    }
    public void setRangTypeActe(int rangTypeActe) {
        this.rangTypeActe = rangTypeActe;
    }
    /**
     * la chaîne de caractères qui permet d'identifier les documents
     * qui doivent être mis sous plis. La valeur par défaut est '',
     * ce qui conviendra pour les documents qui ne comprennent pas
     * un original et une ampliation. Si on utilise la valeur par
     * défaut, tous les documents seront mis dans un fichier dont
     * le nom comporte la chaîne 'SousPlis'.
     */
    private String chaineSousPlis = "";
    String getChaineSousPlis() {
        return chaineSousPlis;
    }
    public void setChaineSousPlis(String chaineSousPlis) {
        this.chaineSousPlis = chaineSousPlis;
    }
    /**
     * la chaîne de caractères qui permet d'identifier les documents
     * qui doivent être conservés par le service et qui sont différents
     * en la forme de ceux qui doivent être mis sous plis (exemple de
     * l'original et de l'ampliation). La valeur par défaut est null.
     * Si on l'utilise, aucun courrier n'ira dans ce type fichier,
     * qui ne sera pas produit. Si on utilise '' <b>et</b> que
     * la chaîne indiquée par chaineSousPlis n'est pas trouvée dans
     * les documents ou a la valeur null, tous les documents seront mis
     * dans un fichier dont le nom comporte la chaîne 'Service'.
     * Ce fichier comporte des pages blanches en lieu et place des
     * versos éventuellement ajoutés pour les documents mis sous plis.
     *
     */
    private String chaineService = null;
    String getChaineService() {
        return chaineService;
    }
    public void setChaineService(String chaineService) {
        this.chaineService = chaineService;
    }
    /**
     * expression régulière permettant de constituer la clé qui
     * identifiera de façon unique chaque courrier. Elle doit comprendre
     * au moins une zone de capture. S'il y en a plusieurs, elles seront
     * concaténées pour établir la clé.
     */
    private String regexpCle;
    String getRegexpCle() {
        return regexpCle;
    }
    public void setRegexpCle(String regexpCle) {
        this.regexpCle = regexpCle;
    }
    /**
     * préfixe à ajouter en tête de la clé. La valeur par défaut
     * est une chaîne vide. On précisera la clé uniquement pour
     * différencier des documents qui ont le même identifiant
     * (un siret par exemple) et qui doivent être mis dans des
     * courriers différents.
     */
    private String prefixeCle = "";
    String getPrefixeCle() {
        return prefixeCle;
    }
    public void setPrefixeCle(String prefixeCle) {
        this.prefixeCle = prefixeCle;
    }
    /**
     * Indicateur que la première page du document doit avoir un rang
     * impair dans le fichier. Si ce n'est pas le cas, une page blanche
     * sera insérée avant la première page. La valeur par défaut est true.
     */
    private boolean pageImpaire = true;
    boolean isPageImpaire() {
        return pageImpaire;
    }
    public void setPageImpaire(boolean pageImpaire) {
        this.pageImpaire = pageImpaire;
    }
    /**
     * Indicateur que le document peut contenir plusieurs pages. La 2ème page
     * et les suivantes ne comportent pas la clé ou ne comportent pas les
     * chaînes permettant d'identifier les documents à mettre sous plis ou
     * ceux qui doivent rester dans le service.
     */
    private boolean plusieursPages = false;
    boolean isPlusieursPages() {
        return plusieursPages;
    }
    public void setPlusieursPages(boolean plusieursPages) {
        this.plusieursPages = plusieursPages;
    }
    /**
     * Nom du type de document qui vient au verso
     * de ce document. La valeur par défaut est null, ce qui
     * signifie qu'il n'y a pas de verso à insérer.
     */
    private String versoInsere = null;
    String getVersoInsere() {
        return versoInsere;
    }
    public void setVersoInsere(String versoInsere) {
        this.versoInsere = versoInsere;
    }
    /**
     * valeur de la rotation à appliquer à ce document.
     * la valeur par défaut est 0. Mettre 90 pour une rotation
     * dans le sens horaire et 270 pour une rotation
     * dans le sens antihoraire.
     */
    private int rotation = 0;
    int getRotation() {
        return rotation;
    }
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    /**
     * place de la date à ajouter à la première page du document.
     * La valeur par défaut est null, aucune date n'est ajoutée.
     * Sinon, on fournit la distance x en mm depuis le bord gauche
     * de la page, et la distance y en mm depuis le bord haut de
     * la page. <p>Pour obtenir ces distances, utiliser LibreOffice
     * Draw sur le document, mettre à zéro les marges de la page
     * et mettre le curseur à l'endroit où on doit placer le début
     * de la date. Les distances sont lues directement sur la ligne
     * du bas de l'écran Draw.</p>
     */
    private Map<String,Float> placeDate = null;
    public Map<String,Float> getPlaceDate() {
        return placeDate;
    }
    public void setPlaceDate(Map<String,Float> placeDate) {
        this.placeDate = placeDate;
        this.placeDate.put("x", placeDate.get("x")*72f/25.4f);
        this.placeDate.put("y", 842f - placeDate.get("y")*72f/25.4f);
    }
    /**
     * place de la signature à ajouter à la première page du document.
     * la valeur par défaut est null, aucune signature n'est ajoutée.
     * Sinon, on fournit la distance x en mm depuis le bord gauche
     * de la page, et la distance y en mm depuis le bord haut de la
     * page.
     */
    private Map<String,Float> placeSignature = null;
    public Map<String, Float> getPlaceSignature() {
        return placeSignature;
    }
    public void setPlaceSignature(Map<String, Float> placeSignature) {
        this.placeSignature = placeSignature;
        this.placeSignature.put("x", this.placeSignature.get("x")*72f/25.4f);
        this.placeSignature.put("y", 842f - this.placeSignature.get("y")*72f/25.4f);
    }
    /**
     * indicateur de l'utilisation du grade pour la signature. La valeur
     * par défaut est true, l'adresse à deux lignes sera placée à l'endroit
     * désigné. Sinon, seule la deuxième ligne, comprenant le nom prénom,
     * sera placée sur le document.
     */
    private boolean avecGrade = true;
    boolean isAvecGrade() {
        return avecGrade;
    }
    public void setAvecGrade(boolean avecGrade) {
        this.avecGrade = avecGrade;
    }
    /**
     * emplacement de l'adresse de l'expéditeur. La valeur par défaut est null,
     * ce qui signifie qu'on n'a pas à replacer cette adresse. Sinon, on fournit :
     * <ul><li>pour le coin basGauche, la distance x et la distance y en mm</li>
     * <li>pour le coint hautDroite, la distance x et la distance y en mm</li></ul>
     */
    private Object adresseExp = null;
    Object getAdresseExp() {
        return adresseExp;
    }
    public void setAdresseExp(Object adresseExp) {
        this.adresseExp = adresseExp;
        this.setRectExp(creeRectangle(this.getAdresseExp()));
    }
    /**
     * le rectangle Itext dans lequel se trouve l'adresse de
     * l'expéditeur. <b>Le paramètre ne doit pas être fourni,
     * il est déduit du paramètre adresseExp.</b>
     */
    private Rectangle rectExp;
    Rectangle getRectExp() {
        return rectExp;
    }
    public void setRectExp(Rectangle rectExp) {
        this.rectExp = rectExp;
    }
    /**
     * indicateur d'effacement de l'adresse expéditeur. La valeur par
     * défaut est false, ce qui signifie qu'on ne l'efface pas.
     */
    private boolean deleteExp = false;
    boolean isDeleteExp() {
        return deleteExp;
    }
    public void setDeleteExp(boolean deleteExp) {
        this.deleteExp = deleteExp;
    }
    /**
     * emplacement de l'adresse du destinataire. La valeur par défaut est nul,
     * ce qui signifie qu'on n'a pas à replacer cette adresse. Sinon, on fournit :
     * <ul><li>pour le coin basGauche, la distance x et la distance y en mm</li>
     * <li>pour le coint hautDroite, la distance x et la distance y en mm</li></ul>
     */
    private Object adresseDest = null;
    Object getAdresseDest() {
        return adresseDest;
    }
    public void setAdresseDest(Object adresseDest) {
        this.adresseDest = adresseDest;
        this.setRectDest(creeRectangle(this.getAdresseDest()));
    }
    /**
     * le rectangle Itext dans lequel se trouve l'adresse du
     * destinataire. <b>Le paramètre ne doit pas être fourni,
     * il est déduit du paramètre adresseDest.</b>
     */
    private Rectangle rectDest;
    Rectangle getRectDest() {
        return rectDest;
    }
    private void setRectDest(Rectangle rectDest) {
        this.rectDest = rectDest;
    }
    /**
     * indicateur d'effacement de l'adresse destinataire. La valeur par
     * défaut est false, ce qui signifie qu'on ne l'efface pas.
     */
    private boolean deleteDest = false;
    boolean isDeleteDest() {
        return deleteDest;
    }
    public void setDeleteDest(boolean deleteDest) {
        this.deleteDest = deleteDest;
    }
    /**
     * dictionnaire des types d'actes, identifiés par leurs noms. <b>Aucun
     * paramètre particulier n'a besoin d'être donné pour construire
     * cette liste.</b>
     */
    private static Map<String,TypeDocument> dico = new HashMap<String, TypeDocument>();
    static Map<String, TypeDocument> getDico() {
        return dico;
    }
    public static void setDico(Map<String, TypeDocument> dico) {
        TypeDocument.dico = dico;
    }
    /**
     * construit une instance de type de document
     */
    TypeDocument(){
    }
    /**
     * construit un rectangle Itext à partir d'un emplacement d'adresse fourni
     * comme paramètre.
     * @param objetAdresse le paramètre fourni, adresseExp ou adresseDest
     * @return le rectangle Itext construit.
     */
    private Rectangle creeRectangle(Object objetAdresse) {
        if (objetAdresse == null) return null;
        LinkedHashMap<String, LinkedHashMap<String, Double>> adresse =
                (LinkedHashMap<String, LinkedHashMap<String, Double>>) objetAdresse;
        return new Rectangle(adresse.get("basGauche").get("x").floatValue()*72f/25.4f,
                842f - adresse.get("basGauche").get("y").floatValue()*72f/25.4f,
                adresse.get("hautDroite").get("x").floatValue()*72f/25.4f,
                842f - adresse.get("hautDroite").get("y").floatValue()*72f/25.4f);
    }
    /**
     * fournit le type de document à partir de son nom
     * @param nom le nom du type de document cherché
     * @return l'instance correspondante au type fourni
     */
    static TypeDocument get(String nom){
        assert (dico.containsKey(nom));
        return dico.get(nom);
    }
    /**
     * indique si le document est le verso d'un autre
     * @return vrai ou faux selon le cas
     */
    boolean isVerso() {
        for(TypeDocument type : values()) {
            if(type.getVersoInsere() != null && type.getVersoInsere().equals(nom)) {
                return true;
            }
        }
        return false;
    }
    /**
     * fournit la liste des types de documents décrits dans le paramétrage
     * @return les valeurs contenus dans le dictionnaire des types de documents
     */
    static Collection<TypeDocument> values() {
        return dico.values();
    }
}
