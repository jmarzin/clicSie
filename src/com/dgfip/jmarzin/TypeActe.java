package com.dgfip.jmarzin;

import java.util.*;

/**
 * Cette classe représente un type d'acte paramétré.
 * Un type d'acte représente une procédure qui peut conduire à
 * générer l'envoi de plusieurs types de documents insérés dans
 * des courriers à un ou plusieurs destinataires.
 * Un type d'acte est créé par interprétation du paramétrage
 * confiée à une bibliothèque externe YAML. C'est elle qui rend
 * nécessaires le constructeur par défaut et les setter de chaque
 * données du type d'acte.
 *
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
public class TypeActe {
    /**
     * nom du type d'acte. Donnée obligatoire qu'il est
     * recommandé de mettre en majuscules. Le setter du nom
     * met à jour le dico des types d'acte
     */
    private String nom;
    String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
        dico.put(nom, this);
    }
    /**
     * Indicateur de mise totale ou partielle du fichier résultat
     * aux normes ClicEsiPlus.
     */
    private boolean clicEsiPlus = true;
    boolean isClicEsiPlus() {
        return clicEsiPlus;
    }
    public void setClicEsiPlus(boolean clicEsiPlus) {
        this.clicEsiPlus = clicEsiPlus;
    }
    /**
     * indicateur de la nécessite de transmettre le fichier
     * à Libre Office en fin de traitement.
     * Si l'indicateur clicEsiPlus a la valeur "faux",
     * une macro LO se chargera de la globalité de la mise
     * aux normes. Sinon, elle n'est prendra qu'une partie,
     * voire uniquement l'écriture du fichier pour s'assurer
     * de sa compatibilité du pdf avec ClicEsiPlus.
     */
    private boolean utiliseLO = false;
    boolean isUtiliseLO() {
        return utiliseLO;
    }
    public void setUtiliseLO(boolean utiliseLO) {
        this.utiliseLO = utiliseLO;
    }
    /**
     * nombre maximum de pages avant découpage en plusieurs
     * parties du fichier résultat. Il est recommandé de la fixer à
     * 400 pour un fichier transmis à LO, et à 1000 pour les autres.
     */
    private int maxPages = 0;
    int getMaxPages() {
        return maxPages;
    }
    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }
    /**
     * Dictionnaire des types d'acte paramétrés. Il est mis à jour
     * à la mise à jour du nom du type d'acte
     */
    private static Map<String,TypeActe> dico = new HashMap<String, TypeActe>();
    private static void setDico(Map<String, TypeActe> dico) {
        TypeActe.dico = dico;
    }
    static Map<String, TypeActe> getDico() {
        return dico;
    }
    /**
     * Créateur d'une instance vide, rendu nécessaire par la bibliothèque YAML
     */
    TypeActe(){
    }
    /**
     * récupère la liste des types d'actes paramétrés
     *
     * @return la collection des types d'actes connus
     */
    static Collection<TypeActe> values() {
        return dico.values();
    }
    /**
     * récupère le type d'acte à partir de son nom
     * @param nom le nom d'un type d'acte
     * @return le type d'acte correspondant
     */
    static TypeActe get(String nom){
        assert (dico.containsKey(nom));
        return dico.get(nom);
    }
    /**
     * récupère la liste ordonnée des types de documents
     * qui réfèrencent le type d'acte
     * @return le tableau des types de documents
     */
    TypeDocument[] typeCourriersOrdonnes() {
        Map<Integer, TypeDocument> dicoTypesParActe = new HashMap<Integer, TypeDocument>();
        for(String entree : TypeDocument.getDico().keySet()) {
            if (TypeDocument.getDico().get(entree).getTypeActe() == this) {
                dicoTypesParActe.put(TypeDocument.getDico().get(entree).getRangTypeActe(),TypeDocument.getDico().get(entree));
            }
        }
        Object[] cles = dicoTypesParActe.keySet().toArray();
        Arrays.sort(cles);
        TypeDocument[] typeDocs= new TypeDocument[dicoTypesParActe.size()];
        for(int i = 0; i < cles.length; i++) {
            typeDocs[i] = dicoTypesParActe.get(cles[i]);
        }
        return typeDocs;
    }
}
