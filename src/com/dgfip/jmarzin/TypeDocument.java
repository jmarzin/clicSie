//package com.dgfip.jmarzin;
//
//import com.itextpdf.text.Rectangle;
//
//public enum TypeDocument {
//    SIE_ATD(TypeActe.SIE_ATD,
//            0,
//            new Rectangle( 30, 842-92,170,842-44),
//            false,
//            new Rectangle(270f,662f,500f,742f),
//            true,
//            ".*N° 3735 Original.*",
//            ".*\\nN° (?:ATD) : (\\d+)\\n.*",
//            "",
//            "N° 3735 Ampliation",
//            "N° 3735 Original",
//            false,
//            true,
//            true),
//    SIE_ATD_NOTIFICATION(TypeActe.SIE_ATD,
//            0,
//            new Rectangle( 30, 842-92,170,842-44),
//            false,
//            new Rectangle(270f,662f,500f,742f),
//            true,
//            ".*N° 3738 Original.*",
//            ".*\\nN° (?:de la notification) : (\\d+)\\n.*",
//            "Notif",
//            "N° 3738 Ampliation",
//            "N° 3738 Original",
//            true,
//            true,
//            true),
//    SIE_ATD_VERSO(null,
//            0,
//            null,
//            false,
//            null,
//            false,
//            ".*ATD-MIRIAM-SP.*",
//            null,
//            null,
//            null,
//            null,
//            false,
//            true,
//            false),
//    SIE_ATD_BULLETIN_REPONSE(TypeActe.SIE_ATD,
//            0,
//            null,
//            false,
//            null,
//            false,
//            ".*BULLETIN-REPONSE A L'AVIS A TIERS DETENTEUR.*",
//            ".*\\nN° (?:ATD) : (\\d+)\\n.*",
//            SIE_ATD.prefixeCle(),
//            "BULLETIN-REPONSE A L'AVIS A TIERS DETENTEUR",
//            null,
//            false,
//            true,
//            false),
//    SIE_CVAE_2807(TypeActe.SIE_CVAE_RELANCE,
//            270,
//            null,
//            false,
//            null,
//            false,
//            ".*RESULTATS? (?:DE )?COMPARAISON CVAE.*",
//            ".*SIRET : (\\d{9} *\\d{5}).*",
//            "Siret",
//            "ETAT 2807",
//            null,
//            false,
//            true,
//            false),
//    SIE_CVAE_RELANCE(TypeActe.SIE_CVAE_RELANCE,
//            0,
//            null,
//            false,
//            null,
//            false,
//            ".*1329RL.*",
//            SIE_CVAE_2807.regexpCle(),
//            SIE_CVAE_2807.prefixeCle(),
//            "1329RL",
//            null,
//            true,
//            true,
//            false),
//    SIE_CVAE_MAJO02(TypeActe.SIE_CVAE_RELANCE,
//            0,
//            null,
//            false,
//            null,
//            false,
//            ".*1329 TL.*",
//            SIE_CVAE_2807.regexpCle(),
//            SIE_CVAE_2807.prefixeCle(),
//            "1329TL",
//            null,
//            true,
//            true,
//            false),
//    SIE_CVAE_MAJ5(TypeActe.SIE_CVAE_RELANCE,
//            0,
//            null,
//            false,
//            null,
//            false,
//            ".*1329MO.*",
//            SIE_CVAE_2807.regexpCle(),
//            SIE_CVAE_2807.prefixeCle(),
//            "1329MO",
//            null,
//            true,
//            true,
//            false);
//
//    private final TypeActe typeActe;
//    private int rotation;
//    private final Rectangle rectExp;
//    private final boolean deleteExp;
//    private final Rectangle rectDest;
//    private final boolean deleteDest;
//    private final String chaineType;
//    private final String regexpCle;
//    private final String prefixeCle;
//    private final String chaineSousPlis;
//    private final String chaineService;
//    private final boolean plusieursPages;
//    private final boolean pageImpaire;
//    private final boolean insereVerso;
//
//    TypeDocument(TypeActe typeActe,
//                 int rotation,
//                 Rectangle rectExp,
//                 boolean deleteExp,
//                 Rectangle rectDest,
//                 boolean deleteDest,
//                 String chaineType,
//                 String regexpCle,
//                 String prefixeCle,
//                 String chaineSousPlis,
//                 String chaineService,
//                 boolean plusieursPages,
//                 boolean pageImpaire,
//                 boolean insereVerso) {
//
//        this.typeActe = typeActe;
//        this.rotation = rotation;
//        this.rectExp = rectExp;
//        this.deleteExp = deleteExp;
//        this.rectDest = rectDest;
//        this.deleteDest = deleteDest;
//        this.chaineType = chaineType;
//        this.regexpCle = regexpCle;
//        this.prefixeCle = prefixeCle;
//        this.chaineSousPlis = chaineSousPlis;
//        this.chaineService = chaineService;
//        this.plusieursPages = plusieursPages;
//        this.pageImpaire = pageImpaire;
//        this.insereVerso = insereVerso;
//        new CTypeDocument(this.name(),typeActe, rotation, rectExp, deleteExp, rectDest, deleteDest, chaineType, regexpCle,
//                prefixeCle, chaineSousPlis, chaineService, plusieursPages, pageImpaire, insereVerso);
//    }
//    private final TypeActe typeActe() { return typeActe;}
//    private final int rotation() { return rotation;}
//    private final Rectangle rectExp() { return rectExp;}
//    private final boolean deleteExp() { return deleteExp;}
//    private final Rectangle rectDest() { return rectDest;}
//    private final boolean deleteDest() { return deleteDest;}
//    private final String chaineType() { return chaineType;}
//    private final String regexpCle() { return regexpCle;}
//    private final String prefixeCle() { return prefixeCle;}
//    private final String chaineSousPlis() { return chaineSousPlis;}
//    private final String chaineService() { return chaineService;}
//    private final boolean plusieursPages() { return plusieursPages;}
//    private final boolean pageImpaire() { return pageImpaire;}
//    private final boolean isInsereVerso() { return insereVerso;}
//}
