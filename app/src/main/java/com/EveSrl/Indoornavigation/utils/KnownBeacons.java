package com.EveSrl.Indoornavigation.utils;

import java.util.ArrayList;
import java.util.List;

public class KnownBeacons {

    public static final String beacon_lato_corto_alto = "25:50";
    public static final String beacon_lato_corto_basso = "1:1";
    public static final String beacon_lato_lungo_destro_alto = "5:3";
    public static final String beacon_lato_lungo_destro_basso = "1:4";
    public static final String beacon_lato_lungo_sinistro_alto = "8408:31297"; // VIOLA SCURO
    public static final String beacon_lato_lungo_sinistro_basso = "1:2";


    public static List<String> getAllAsList(){
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add(beacon_lato_corto_alto);
        arrayList.add(beacon_lato_corto_basso);
        arrayList.add(beacon_lato_lungo_destro_alto);
        arrayList.add(beacon_lato_lungo_destro_basso);
        arrayList.add(beacon_lato_lungo_sinistro_alto);
        arrayList.add(beacon_lato_lungo_sinistro_basso);

        return arrayList;
    }
}
