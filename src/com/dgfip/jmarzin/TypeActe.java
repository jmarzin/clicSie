package com.dgfip.jmarzin;

/**
 * Created by jmarzin-cp on 10/05/2017.
 */
public enum TypeActe {
    SIE_ATD,
    SIE_CVAE_RELANCE;

    TypeActe() {
    }

    public final CTypeDocument[] typeCourriersOrdonnes() {
        if(this == SIE_ATD) {
            return new CTypeDocument[] {CTypeDocument.get("SIE_ATD"),
                    CTypeDocument.get("SIE_ATD_BULLETIN_REPONSE"),
                    CTypeDocument.get("SIE_ATD_NOTIFICATION")};
        } else if (this == SIE_CVAE_RELANCE) {
            return new CTypeDocument[] {CTypeDocument.get("SIE_CVAE_RELANCE"),
                    CTypeDocument.get("SIE_CVAE_MAJ5"),
                    CTypeDocument.get("SIE_CVAE_MAJO02"),
                    CTypeDocument.get("SIE_CVAE_2807")};
        } else return new CTypeDocument[] {};
    }
}
