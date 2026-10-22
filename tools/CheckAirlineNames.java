package com.nearsky.tv;
import java.nio.file.*;
import javax.xml.parsers.*;
import java.io.*;
import org.json.*;
import org.w3c.dom.*;

public final class CheckAirlineNames {
    private static int checks;
    private static void check(boolean pass,String message){checks++;if(!pass)throw new AssertionError(message);}
    private static String label(String svg)throws Exception{
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        Document doc=factory.newDocumentBuilder().parse(new ByteArrayInputStream(svg.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        NodeList nodes=doc.getElementsByTagName("text");
        for(int i=0;i<nodes.getLength();i++){Element e=(Element)nodes.item(i);if(e.getAttribute("id").equals("airline-name"))return e.getTextContent();}
        throw new AssertionError("Missing airline name element");
    }
    public static void main(String[] args)throws Exception{
        check(AirlineNames.known("IGO").equals("IndiGo"),"IndiGo brand spelling");
        check(AirlineNames.known("SEJ").equals("SpiceJet"),"SpiceJet brand spelling");
        check(AirlineNames.known("AIC").equals("Air India"),"Air India name");
        check(AirlineNames.known("ZZZ").isEmpty(),"Do not invent names for unassigned codes");
        check(AirlineNames.clean("  Air   India  ").equals("Air India"),"Normalize spaces");
        check(AirlineNames.clean("null").isEmpty(),"Null string is not an airline");
        check(AirlineNames.clean("x".repeat(100)).length()==80,"Bound provider text length");
        check(label(LiveryDesign.svg("IGO")).equals("IndiGo"),"Fuselage has real airline name");
        check(label(LiveryDesign.svg("ZZZ")).equals("ZZZ"),"Code fallback is preserved");
        check(label(LiveryDesign.svg("ABC","A & B <Air>")).equals("A & B <Air>"),"Escape provider text as XML");
        check(label(LiveryDesign.withName(LiveryDesign.svg("ABC"),"ABC","New $ Air \\ Co")).equals("New $ Air \\ Co"),"Safe override replacement and cache refresh payload");
        check(AirlineNameRepository.parse("AIC",new JSONObject("{\"response\":[{\"icao\":\"IGO\",\"name\":\"Wrong airline\"},{\"icao\":\"AIC\",\"name\":\"Air India\"}]}")).equals("Air India"),"Match exact ICAO code, not the first result");
        check(AirlineNameRepository.parse("AIC",new JSONObject("{\"response\":[{\"icao\":\"IGO\",\"name\":\"Wrong airline\"}]}")).isEmpty(),"Reject another carrier's name");
        check(AirlineNameRepository.parse("AIC",new JSONObject("{\"response\":\"unknown airline\"}")).isEmpty(),"Unknown API response remains unnamed");
        for(String code:new String[]{"AIC","IGO","SEJ","AKJ","AXB","BAW","DLH","ETD","QTR","SIA","THY","UAE"}){
            JSONObject response=new JSONObject(Files.readString(Path.of(args[0],code+".json")));
            check(!AirlineNameRepository.parse(code,response).isEmpty(),"Parse verified live provider response: "+code);
        }
        System.out.println("PASS: "+checks+" airline-name and SVG checks");
    }
}
