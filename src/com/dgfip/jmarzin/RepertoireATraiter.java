package com.dgfip.jmarzin;

import com.itextpdf.text.pdf.PdfReader;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dgfip.jmarzin.ClicSie.jLabel;
import static com.dgfip.jmarzin.ClicSie.log;

class RepertoireATraiter {

    private File repertoire;
    File getRepertoire() { return repertoire;}

    private List<FichierPdfATraiter> fichiersPdf = new ArrayList<FichierPdfATraiter>();
    List<FichierPdfATraiter> getFichiersPdf() { return fichiersPdf;}

    private List<String> fichiersADeplacer = new ArrayList<String>();
    List<String> getFichiersADeplacer() { return fichiersADeplacer;}

    private boolean exclus(String nomFichier) {
        for(TypeActe typeActe : TypeActe.values()) {
            if (nomFichier.startsWith(typeActe.getNom() + "__")) return true;
        }
        return false;
    }

    boolean isSignatureNecessaire() {
        return signatureNecessaire;
    }

    public String[] getSignature() {
        return signature;
    }

    private String[] signature = null;

    private boolean signatureNecessaire = false;

    Set<String> verifPresenceVerso() {
        Set<String> listeTypesDocument = new HashSet<String>();
        Set<String> listeVersosManquants = new HashSet<String>();
        for (FichierPdfATraiter fichier : fichiersPdf) {
            listeTypesDocument.add(fichier.getTypeFichier().getNom());
        }
        for(String type : listeTypesDocument) {
            String nomVerso = TypeDocument.get(type).getVersoInsere();
            if(nomVerso != null && !listeTypesDocument.contains(nomVerso)) {
                    listeVersosManquants.add(nomVerso);
            }
        }
        return listeVersosManquants;
    }

    PdfReader getVersoParNom(TypeDocument typeDocument) {
        for(FichierPdfATraiter fic : fichiersPdf) {
            if (fic.getTypeFichier() == typeDocument) return fic.getLecteurPdf();
        }
        return null;
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
                if (fic.getTypeFichier() == null) {
                    log(String.format("Le fichier %s n'est pas reconnu !", listeFichiers[i].getName()));
                } else {
                    this.fichiersPdf.add(fic);
                    TypeDocument typeFichier = fic.getTypeFichier();
                    if (!typeFichier.isVerso()) {
                        if (typeFichier.getPlaceSignature() != null && typeFichier.getTypeActe().isClicEsiPlus()) {
                            signatureNecessaire = true;
                            try {
                                this.signature = UtileFichier.lit(repertoire.getCanonicalFile() + File.separator + "signature.txt");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        this.fichiersADeplacer.add(listeFichiers[i].getName());
                    }
                }
            } else {
                    this.fichiersADeplacer.add(listeFichiers[i].getName());
            }
        }
    }
}
