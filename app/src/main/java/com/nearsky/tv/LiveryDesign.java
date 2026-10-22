package com.nearsky.tv;

import java.util.Locale;

/** Original, deterministic SVG artwork for every A-Z three-letter operator code. No brand assets. */
public final class LiveryDesign {
    private LiveryDesign() { }
    public static String normalized(String code) {
        if (code == null) return "";
        String clean = code.trim().toUpperCase(Locale.ROOT);
        return clean.matches("[A-Z]{3}") ? clean : "";
    }
    public static String fromCallsign(String callsign) {
        String clean = callsign == null ? "" : callsign.trim().toUpperCase(Locale.ROOT);
        // A registration such as N123AB or VT-ANB is not evidence of an airline.
        return clean.matches("[A-Z]{3}[0-9][A-Z0-9]*") ? clean.substring(0, 3) : "";
    }
    private static int index(String code) {
        String c = normalized(code);
        return c.isEmpty() ? -1 : (c.charAt(0)-'A')*676 + (c.charAt(1)-'A')*26 + c.charAt(2)-'A';
    }
    public static String primary(String code) {
        int n = index(code);
        return n < 0 ? "#7397A4" : hsl((n * 137.507764 + 24) % 360, .64, .55);
    }
    public static String accent(String code) {
        int n = index(code);
        return n < 0 ? "#B5CCBF" : hsl((n * 137.507764 + 174) % 360, .76, .68);
    }
    private static String hsl(double h, double s, double l) {
        double c = (1 - Math.abs(2*l-1))*s, x = c*(1-Math.abs((h/60)%2-1)), m=l-c/2;
        double r=0,g=0,b=0;
        if(h<60){r=c;g=x;}else if(h<120){r=x;g=c;}else if(h<180){g=c;b=x;}
        else if(h<240){g=x;b=c;}else if(h<300){r=x;b=c;}else{r=c;b=x;}
        return String.format(Locale.ROOT,"#%02X%02X%02X",Math.round((r+m)*255),Math.round((g+m)*255),Math.round((b+m)*255));
    }
    private static String xml(String value){return value.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;");}
    private static String nameElement(String code,String airlineName){
        String label=AirlineNames.clean(airlineName);
        if(label.isEmpty())label=code.isEmpty()?"NEARSKY":code;
        double size=Math.min(32,520.0/(Math.max(1,label.codePointCount(0,label.length()))*.68));
        return "<text id=\"airline-name\" x=\"350\" y=\"278\" font-family=\"sans-serif\" font-size=\""+String.format(Locale.ROOT,"%.2f",size)+"\" font-weight=\"bold\" letter-spacing=\"0.4\" fill=\"#153040\">"+xml(label)+"</text>";
    }
    public static String withName(String svg,String code,String name){
        return svg.replaceFirst("<text id=\"airline-name\"[^>]*>[^<]*</text>",java.util.regex.Matcher.quoteReplacement(nameElement(normalized(code),name)));
    }
    public static String svg(String operator) { return svg(operator,AirlineNames.known(operator)); }
    public static String svg(String operator,String airlineName) {
        String code = normalized(operator), mark = code.isEmpty() ? "NS" : code;
        int n = index(code), variant = n < 0 ? 0 : n % 6;
        String a = primary(code), b = accent(code);
        String body = "M100 229 C245 222 647 221 929 225 C991 223 1037 242 1085 273 Q1112 292 1070 303 C1026 315 927 315 838 313 L260 302 Q154 294 100 229 Z";
        String tail = "M160 244 L73 62 Q68 51 83 51 L121 51 L326 246 Z";
        StringBuilder out = new StringBuilder(7000);
        out.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1200\" height=\"440\" viewBox=\"0 0 1200 440\">")
           .append("<title>NearSky original concept livery: ").append(mark).append("</title>")
           .append("<desc>Fictional airline artwork. Generic airliner silhouette, not an official livery or an exact aircraft depiction.</desc>")
           .append("<defs><linearGradient id=\"body\" x1=\"0\" y1=\"0\" x2=\"0\" y2=\"1\"><stop offset=\"0\" stop-color=\"#FFFFFF\"/><stop offset=\".55\" stop-color=\"#E4EBEC\"/><stop offset=\"1\" stop-color=\"#A4B9C3\"/></linearGradient>")
           .append("<linearGradient id=\"tail\" x1=\"0\" y1=\"0\" x2=\"1\" y2=\"1\"><stop stop-color=\"").append(a).append("\"/><stop offset=\"1\" stop-color=\"").append(b).append("\"/></linearGradient>")
           .append("<clipPath id=\"bodyclip\"><path d=\"").append(body).append("\"/></clipPath><clipPath id=\"tailclip\"><path d=\"").append(tail).append("\"/></clipPath></defs>")
           .append("<ellipse cx=\"602\" cy=\"367\" rx=\"454\" ry=\"20\" fill=\"#020B12\" opacity=\".22\"/>")
           .append("<path d=\"").append(tail).append("\" fill=\"url(#tail)\"/>")
           .append("<g clip-path=\"url(#tailclip)\" fill=\"").append(b).append("\">");
        switch(variant) {
            case 0: out.append("<path d=\"M54 148 L235 49 L264 79 L71 193 Z\"/><path d=\"M90 224 L290 115 L309 143 L122 254 Z\" opacity=\".6\"/>"); break;
            case 1: out.append("<circle cx=\"167\" cy=\"130\" r=\"61\" fill=\"none\" stroke=\"").append(b).append("\" stroke-width=\"22\"/><circle cx=\"167\" cy=\"130\" r=\"19\"/>"); break;
            case 2: out.append("<path d=\"M79 243 L169 58 L255 243 L221 243 L168 129 L117 243 Z\"/>"); break;
            case 3: out.append("<path d=\"M65 101 Q149 188 269 134 L288 161 Q164 225 78 144 Z\"/><path d=\"M94 170 Q177 253 302 198 L322 224 Q177 286 101 211 Z\" opacity=\".65\"/>"); break;
            case 4: out.append("<path d=\"M125 70 L228 145 L163 215 L65 138 Z\" fill=\"none\" stroke=\"").append(b).append("\" stroke-width=\"20\"/>"); break;
            default: out.append("<path d=\"M58 91 L270 185 L253 210 L68 136 Z\"/><path d=\"M99 224 L201 56 L230 80 L130 245 Z\" opacity=\".75\"/>");
        }
        out.append("</g><path d=\"").append(body).append("\" fill=\"url(#body)\"/>")
           .append("<g clip-path=\"url(#bodyclip)\"><path d=\"M73 275 C331 327 520 244 810 269 Q968 283 1134 252 L1142 335 L74 335 Z\" fill=\"").append(a).append("\"/>")
           .append("<path d=\"M98 266 C332 316 557 238 813 261 Q985 278 1122 246\" fill=\"none\" stroke=\"").append(b).append("\" stroke-width=\"8\"/>");
        if(variant%2==0) out.append("<path d=\"M195 210 L305 210 L400 321 L290 321 Z\" fill=\"").append(a).append("\" opacity=\".8\"/>");
        out.append("</g><g fill=\"#243E50\">");
        for(int i=0;i<33;i++) out.append("<rect x=\"").append(338+i*17).append("\" y=\"240\" width=\"8\" height=\"10\" rx=\"2\"/>");
        out.append("<path d=\"M973 238 L1008 243 L1024 255 L991 253 Z\"/><path d=\"M967 238 L985 253 L969 253 Z\"/></g>")
           .append("<path d=\"M516 280 L769 370 L842 370 L647 271 Z\" fill=\"#8BA5B2\"/><path d=\"M521 278 L761 357 L812 357 L646 269 Z\" fill=\"#D4E0E4\"/>")
           .append("<path d=\"M691 325 Q728 311 774 325 L786 350 Q751 369 704 354 Z\" fill=\"").append(a).append("\"/>")
           .append("<ellipse cx=\"779\" cy=\"338\" rx=\"10\" ry=\"15\" fill=\"#233C4A\"/>")
           .append("<path d=\"M186 267 L89 294 L64 294 L150 257 Z\" fill=\"#BDCFD7\"/>")
           .append(nameElement(code,airlineName))
           .append("<text x=\"158\" y=\"218\" font-family=\"sans-serif\" font-size=\"20\" font-weight=\"bold\" fill=\"#FFFFFF\">").append(mark).append("</text></svg>");
        return out.toString();
    }
}
