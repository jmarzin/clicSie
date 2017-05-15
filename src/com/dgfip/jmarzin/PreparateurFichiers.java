package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.*;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.dgfip.jmarzin.ClicSie.jLabel;

class PreparateurFichiers {

    private LotPrepare lotPrepare = new LotPrepare();

    LotPrepare getLotPrepare() {
        return lotPrepare;
    }

    private String getCle(String chaine, TypeDocument typeDocument) {
        String cle = "";
        Pattern pattern = Pattern.compile(typeDocument.getRegexpCle(), Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(chaine);
        if (matcher.matches()) {
            cle = typeDocument.getTypeActe().name() + "_" + typeDocument.getPrefixeCle() + matcher.group(1);
        }
        return cle.replaceAll(" ", "");
    }

    PreparateurFichiers(RepertoireATraiter repertoireATraiter) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.lotPrepare = new LotPrepare();
        int nbFichiers = repertoireATraiter.getFichiersPdf().size();
        int iFichiers = 1;
        for (FichierPdfATraiter fichier : repertoireATraiter.getFichiersPdf()) {
            jLabel.setText(String.format("Fichiers traités : %d/%d", iFichiers, nbFichiers));
            iFichiers++;
            PdfReader lecteurPdf = fichier.getLecteurPdf();
            TypeDocument typeDocument = fichier.getTypeFichier();
            if (typeDocument != null && !typeDocument.getNom().toLowerCase().contains("verso")) {
                String cle = "";
                TypeFichierProduit typeFichierProduit = null;
                DocumentAdm documentAdm = new DocumentAdm(typeDocument);
                for (int ipage = 1; ipage <= lecteurPdf.getNumberOfPages(); ipage++) {
                    String chaine = fichier.getChaine((ipage));
                    if (typeDocument.getChaineSousPlis() != null && chaine.contains(typeDocument.getChaineSousPlis())) {
                        typeFichierProduit = TypeFichierProduit.SousPlis;
                    } else if (typeDocument.getChaineService() != null && chaine.contains(typeDocument.getChaineService())) {
                        typeFichierProduit = TypeFichierProduit.Service;
                    }
                    String cleN = getCle(chaine, typeDocument);
                    if (cleN.isEmpty()) {
                        if (!typeDocument.isPlusieursPages()) {
                            continue;
                        }
                    } else {
                        cle = cleN;
                    }
                    if(documentAdm.getTypeFichierProduit() == null) {
                        documentAdm.setTypeFichierProduit(typeFichierProduit);
                        documentAdm.setCle(cle);
                    } else if(documentAdm.getTypeFichierProduit() != null &&
                                (!documentAdm.getCle().equals(cle) || documentAdm.getTypeFichierProduit() != typeFichierProduit)) {
                        lotPrepare.ajout(documentAdm);
                        documentAdm = new DocumentAdm((typeDocument));
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
