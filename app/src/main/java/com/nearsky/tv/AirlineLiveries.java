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
            case "AAL": return "#174A80";
            case "DAL": return "#09254B";
            case "UAL": return "#005DAA";
            case "SWA": return "#2454A4";
            case "ASA": return "#123D5B";
            case "JBU": return "#122C54";
            case "FFT": return "#08754A";
            case "AAY": return "#125BA3";
            case "ACA": return "#181B1C";
            case "WJA": return "#003C56";
            case "VIR": return "#CE143E";
            case "EZY": return "#F36B21";
            case "RYR": return "#123367";
            case "EIN": return "#007F78";
            case "EWG": return "#89214A";
            case "CFG": return "#D8A51D";
            case "KLM": return "#008CCB";
            case "TRA": return "#00A650";
            case "IBE": return "#CE182B";
            case "VLG": return "#5B5C5E";
            case "AEA": return "#173E80";
            case "ITY": return "#0764AF";
            case "SWR": return "#DC1C2C";
            case "AUA": return "#D71332";
            case "BEL": return "#BD1D43";
            case "SAS": return "#132A85";
            case "FIN": return "#101E4E";
            case "NAX": return "#DD2733";
            case "TAP": return "#D81D2D";
            case "PGT": return "#E7B719";
            case "AFL": return "#103C73";
            case "LOT": return "#123969";
            case "AEE": return "#103969";
            case "KAC": return "#143F7C";
            case "ELY": return "#12346D";
            case "RJA": return "#34363B";
            case "MEA": return "#006C46";
            case "MSR": return "#153F7D";
            case "ETH": return "#BE2130";
            case "KQA": return "#CA1D36";
            case "SAA": return "#123C72";
            case "RAM": return "#B71D34";
            case "TGW": return "#F7CC22";
            case "MAS": return "#133F75";
            case "AXM": return "#E3212C";
            case "HVN": return "#08788A";
            case "GIA": return "#0A6979";
            case "PAL": return "#0C3679";
            case "CEB": return "#05A4C0";
            case "CPA": return "#00645B";
            case "CCA": return "#153B72";
            case "CES": return "#193A76";
            case "CSN": return "#16659B";
            case "CHH": return "#BD2035";
            case "AAR": return "#55565A";
            case "QFA": return "#D62431";
            case "VOZ": return "#C91D3A";
            case "JST": return "#30343B";
            case "ANZ": return "#171C1E";
            case "FJI": return "#483322";
            case "LAN": return "#192E63";
            case "AVA": return "#D4202D";
            case "CMP": return "#153E7B";
            case "AMX": return "#102F55";
            case "VOI": return "#202127";
            case "GLO": return "#EF6E25";
            case "AZU": return "#17376C";
            default: return null;
        }
    }
    static boolean contains(String code){return primary(code)!=null;}
}
