package com.nearsky.tv;
import java.nio.file.*;
import org.json.*;
public final class CheckRoutes {
    private static int checks;
    private static void check(boolean pass,String message){checks++;if(!pass)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        JSONObject fixture=new JSONObject(Files.readString(Path.of(args[0])));
        FlightRoute r=FlightRoute.parse(" AIC101 ",fixture);
        check(r!=null&&r.origin.code.equals("DEL")&&r.destination.code.equals("JFK"),"Live provider origin/destination");
        check(r.origin.city.equals("New Delhi")&&r.destination.city.equals("New York"),"City names");
        check(FlightRoute.parse("AI101",fixture)!=null,"Exact IATA alias");
        check(FlightRoute.parse("aic101",fixture)!=null,"Normalize callsign");
        check(FlightRoute.parse("AIC102",fixture)==null,"Reject different flight");
        check(FlightRoute.parse("",fixture)==null,"Reject missing callsign");
        check(FlightRoute.callsign("AIC101/../../").isEmpty(),"Reject unsafe callsign");
        check(FlightRoute.callsign("VT-ANB").isEmpty(),"Do not query registration");
        check(FlightRoute.parse("AIC101",new JSONObject("{\"response\":\"unknown callsign\"}"))==null,"Unknown response");
        JSONObject route=fixture.getJSONObject("response").getJSONObject("flightroute");
        route.getJSONObject("origin").put("iata_code",JSONObject.NULL);
        check(FlightRoute.parse("AIC101",fixture).origin.code.equals("VIDP"),"ICAO airport fallback");
        route.getJSONObject("origin").put("municipality",JSONObject.NULL);
        check(FlightRoute.parse("AIC101",fixture).origin.city.equals("Indira Gandhi International Airport"),"Airport name fallback");
        route.put("destination",new JSONObject());
        check(FlightRoute.parse("AIC101",fixture)==null,"Reject unusable endpoint");
        route.remove("destination");
        check(FlightRoute.parse("AIC101",fixture)==null,"Reject partial route");
        check(FlightRoute.parse("AIC101",new JSONObject())==null,"Reject malformed schema");
        System.out.println(checks+" route checks passed");
    }
}
