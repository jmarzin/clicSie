package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.dgfip.jmarzin.ClicSie.jLabel;

/**
 * Cette classe représente un préparateur de fichiers qui :
 * <ul>
 *     <li>crée l'instance de lot global préparé pour les traitements ultéieurs,</li>
 *     <li>exploite le paramétrage pour constituer les documents administratifs de chaque fichier,</li>
 *     <li>le type de fichier produit (à mettre sous plis ou à conserver),</li>
 *     <li>la clé qui rendra unique chaque courrier,</li>
 *     <li>et en demande le classement au bon niveau dans le lot préparé,</li>
 *     <li>en prenant en compte par ailleurs le type de document et</li>
 *     <li>le type d'acte, unique pour chaque fichier.</li>
 * </ul>
 * @author Jacques Marzin
 * @version 1.0
 * @since 21 mai 2017
 */
class PreparateurFichiers {
    /**
     * Le lot préparé associé au préparateur
     */
    private LotPrepare lotPrepare = new LotPrepare();
    LotPrepare getLotPrepare() {
        return lotPrepare;
    }
    /**
     * Le calcul de la clé qui rendra unique chaque courrier.
     * Son emplacement est fixé par le paramétrage
     * @param chaine le contenu du document administratif
     * @param typeDocument le type de document
     * @return la clé calculée. Si elle n'est pas trouvée, elle est vide
     */
    private String getCle(String chaine, TypeDocument typeDocument) {
        String cle = "";
        Pattern pattern = Pattern.compile(typeDocument.getRegexpCle(), Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(chaine);
        if (matcher.matches()) {
            cle = typeDocument.getTypeActe().getNom() + "_" + typeDocument.getPrefixeCle();
            for(int i = 1; i <= matcher.groupCount(); i++) {
                cle = cle + matcher.group(i);
            }
        }
        return cle.replaceAll(" ", "");
    }
    /**
     * Crée une instance de préparateur
     */
    PreparateurFichiers() {
        this.lotPrepare = new LotPrepare();
        int nbFichiers = RepertoireATraiter.getFichiersPdf().size();
        //initialise un compteur pour mesurer l'avancement
        int iFichiers = 1;
        //pour chaque fichier de la liste
        for (FichierPdfATraiter fichier : RepertoireATraiter.getFichiersPdf()) {
            jLabel.setText(String.format("Fichiers traités : %d/%d", iFichiers, nbFichiers));
            iFichiers++;
            PdfReader lecteurPdf = fichier.getLecteurPdf();
            TypeDocument typeDocument = fichier.getTypeFichier();
            if (typeDocument != null && !typeDocument.getNom().toLowerCase().contains("verso")) {
                // les fichiers verso sont ignorés
                String cle = "";
                TypeFichierProduit typeFichierProduit = null;
                //création du nouveau document administratif
                DocumentAdm documentAdm = new DocumentAdm(fichier.getRepertoireATraiter(), typeDocument);
                //pour chaque page
                for (int ipage = 1; ipage <= lecteurPdf.getNumberOfPages(); ipage++) {
                    String chaine = fichier.getChaine((ipage));
                    //récupération du type de fichier produit
                    if (typeDocument.getChaineSousPlis() != null && chaine.contains(typeDocument.getChaineSousPlis())) {
                        typeFichierProduit = TypeFichierProduit.SousPlis;
                    } else if (typeDocument.getChaineService() != null && chaine.contains(typeDocument.getChaineService())) {
                        typeFichierProduit = TypeFichierProduit.Service;
                    }
                    //récupération de la clé
                    String cleN = getCle(chaine, typeDocument);
                    if (cleN.isEmpty()) {
                        //traitement des documents de plusieurs pages
                        if (!typeDocument.isPlusieursPages()) {
                            continue;
                        }
                    } else {
                        cle = cleN;
                    }
                    //ajout de la ou des pages au document administratif
                    //et ajout au lot global préparé
                    if(documentAdm.getTypeFichierProduit() == null) {
                        documentAdm.setTypeFichierProduit(typeFichierProduit);
                        documentAdm.setCle(cle);
                    } else if(documentAdm.getTypeFichierProduit() != null &&
                                (!documentAdm.getCle().equals(cle) || documentAdm.getTypeFichierProduit() != typeFichierProduit)) {
                        lotPrepare.ajout(documentAdm);
                        documentAdm = new DocumentAdm(fichier.getRepertoireATraiter(), typeDocument);
                        documentAdm.setTypeFichierProduit(typeFichierProduit);
                        documentAdm.setCle(cle);
                    }
                    documentAdm.ajout(new PageLue(fichier,ipage));
                }
                lotPrepare.ajout(documentAdm);
            }
        }
    }
}
