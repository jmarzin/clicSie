package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.dgfip.jmarzin.ClicSie.jLabel;

class RepertoireATraiter {

    private boolean versoAtdSieNecessaire = false;
    boolean isVersoAtdSieNecessaire() { return versoAtdSieNecessaire;}

    private File repertoire;
    File getRepertoire() { return repertoire;}

    private List<FichierPdfATraiter> fichiersPdf = new ArrayList<FichierPdfATraiter>();
    List<FichierPdfATraiter> getFichiersPdf() { return fichiersPdf;}

    private List<String> fichiersADeplacer = new ArrayList<String>();
    List<String> getFichiersADeplacer() { return fichiersADeplacer;}

    private PdfReader versoAtdSie = null;
    PdfReader getVersoAtdSie() { return versoAtdSie;}

    private boolean exclus(String nomFichier) {
        for(TypeActe typeActe : TypeActe.values()) {
            if (nomFichier.startsWith(typeActe.name() + "__")) return true;
        }
        return false;
    }

    RepertoireATraiter(JFileChooser fc) {
        this.repertoire = fc.getSelectedFile().getAbsoluteFile();
        File[] listeFichiers = fc.getSelectedFile().listFiles(new OnlyFile("pdf"));
        int nbFichiers = 0;
        if(listeFichiers != null) nbFichiers = listeFichiers.length;
        for(int i = 0; i < nbFichiers; i++) {
            jLabel.setText((i+1) + " fichier(s) traité()s");
            if(!exclus(listeFichiers[i].getName())) {
                FichierPdfATraiter fic = new FichierPdfATraiter(listeFichiers[i]);
                this.fichiersPdf.add(fic);
                if(ClicSie.getEnsembleEvenements().contains(TypeActe.SIE_ATD)) versoAtdSieNecessaire = true;
                CTypeDocument typeFichier = fic.getTypeFichier();
                if (typeFichier == CTypeDocument.get("SIE_ATD_VERSO")) { //;"Verso") {
                    this.versoAtdSie = fic.getLecteurPdf();
                } else if (typeFichier != null) {
                    this.fichiersADeplacer.add(listeFichiers[i].getName());
                }
            } else {
                this.fichiersADeplacer.add(listeFichiers[i].getName());
            }
        }
    }
}
