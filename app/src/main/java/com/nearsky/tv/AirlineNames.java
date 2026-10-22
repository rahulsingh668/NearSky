package com.nearsky.tv;
import java.util.*;
/** Verified offline names; exact-code API lookups extend this small seed catalogue. */
public final class AirlineNames {
    private AirlineNames(){}
    private static final Map<String,String> KNOWN;
    static { Map<String,String> names=new HashMap<>();
        names.put("AIC","Air India");
        names.put("AKJ","Akasa Air");
        names.put("AXB","Air India Express");
        names.put("BAW","British Airways");
        names.put("DLH","Lufthansa");
        names.put("ETD","Etihad Airways");
        names.put("IGO","IndiGo");
        names.put("QTR","Qatar Airways");
        names.put("SEJ","SpiceJet");
        names.put("SIA","Singapore Airlines");
        names.put("THY","Turkish Airlines");
        names.put("UAE","Emirates");
        KNOWN=Collections.unmodifiableMap(names);
    }
    public static String known(String code){return KNOWN.getOrDefault(LiveryDesign.normalized(code),"");}
    public static String clean(String value){
        if(value==null)return "";
        StringBuilder result=new StringBuilder();
        value.codePoints().filter(c->c>=32&&!Character.isISOControl(c)&&Character.getType(c)!=Character.FORMAT&&!(c>=0xD800&&c<=0xDFFF)&&c!=0xFFFE&&c!=0xFFFF).limit(80).forEach(result::appendCodePoint);
        String name=result.toString().trim().replaceAll("\\s+"," ");
        return name.equalsIgnoreCase("null")?"":name;
    }
    public static String display(String code,String providerName){
        String name=clean(providerName);
        if(name.isEmpty())return "";
        // Match the public brand spelling on the carriers' official websites.
        if(code.equals("IGO"))return "IndiGo";
        if(code.equals("SEJ"))return "SpiceJet";
        return name;
    }
}
