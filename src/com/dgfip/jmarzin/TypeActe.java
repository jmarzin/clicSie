package com.dgfip.jmarzin;

/**
 * Created by jmarzin-cp on 10/05/2017.
 */
public enum TypeActe {
    SIE_ATD,
    SIE_CVAE_RELANCE;

    TypeActe() {
    }

    public final TypeDocument[] typeCourriersOrdonnes() {
        if(this == SIE_ATD) {
            return new TypeDocument[] {TypeDocument.get("SIE_ATD"),
                    TypeDocument.get("SIE_ATD_BULLETIN_REPONSE"),
                    TypeDocument.get("SIE_ATD_NOTIFICATION")};
        } else if (this == SIE_CVAE_RELANCE) {
            return new TypeDocument[] {TypeDocument.get("SIE_CVAE_RELANCE"),
                    TypeDocument.get("SIE_CVAE_MAJ5"),
                    TypeDocument.get("SIE_CVAE_MAJO02"),
                    TypeDocument.get("SIE_CVAE_2807")};
        } else return new TypeDocument[] {};
    }
}
