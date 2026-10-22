package com.nearsky.tv;

/** Bundled standard-livery catalogue; representative airline schemes, not per-aircraft paint. */
final class AirlineLiveries {
    private AirlineLiveries(){}
    static String primary(String code){
        switch(LiveryDesign.normalized(code)){
            case "IGO": return "#203F91";
            case "AIC": return "#D31B32";
            case "SEJ": return "#E51D2A";
            case "AKJ": return "#F36D30";
            case "AXB": return "#F26B32";
            case "BAW": return "#132952";
            case "DLH": return "#071D49";
            case "ETD": return "#A78C50";
            case "QTR": return "#681B46";
            case "SIA": return "#092A50";
            case "THY": return "#D71F2B";
            case "UAE": return "#B89C54";
            case "KAL": return "#102F5D";
            case "UPS": return "#472817";
            case "FDX": return "#4D148C";
            case "AFR": return "#102450";
            case "ANA": return "#0753A5";
            case "JAL": return "#131721";
            case "SVA": return "#087E52";
            case "THA": return "#5A237A";
            case "ALK": return "#006B9E";
            case "OMA": return "#077D8C";
            case "FDB": return "#057BC1";
            case "GFA": return "#092A46";
            default: return null;
        }
    }
    static boolean contains(String code){return primary(code)!=null;}
}
