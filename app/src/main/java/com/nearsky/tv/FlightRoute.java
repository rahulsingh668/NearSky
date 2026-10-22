package com.nearsky.tv;

import java.util.Locale;
import org.json.JSONObject;

/** Callsign-matched reference route; never inferred from the aircraft position. */
final class FlightRoute {
    final Airport origin,destination;
    FlightRoute(Airport origin,Airport destination){this.origin=origin;this.destination=destination;}
    static final class Airport {
        final String code,city;
        Airport(JSONObject value){
            String iata=clean(value,"iata_code").toUpperCase(Locale.US);
            String icao=clean(value,"icao_code").toUpperCase(Locale.US);
            code=iata.matches("[A-Z]{3}")?iata:icao.matches("[A-Z0-9]{4}")?icao:"—";
            String municipality=clean(value,"municipality");
            city=municipality.isEmpty()?clean(value,"name"):municipality;
        }
    }
    static String callsign(String value){
        String s=value==null?"":value.trim().toUpperCase(Locale.US);
        return s.matches("[A-Z]{2,3}[0-9][A-Z0-9]{0,8}")?s:"";
    }
    private static String clean(JSONObject value,String key){return AirlineNames.clean(value.optString(key,""));}
    static FlightRoute parse(String requested,JSONObject root){
        String key=callsign(requested);
        JSONObject response=root.optJSONObject("response");
        JSONObject route=response==null?null:response.optJSONObject("flightroute");
        if(key.isEmpty()||route==null)return null;
        boolean matches=false;
        for(String field:new String[]{"callsign","callsign_icao","callsign_iata"})
            if(key.equals(callsign(route.optString(field,""))))matches=true;
        JSONObject origin=route.optJSONObject("origin"),destination=route.optJSONObject("destination");
        if(!matches||origin==null||destination==null)return null;
        Airport from=new Airport(origin),to=new Airport(destination);
        if(from.code.equals("—")&&from.city.isEmpty()||to.code.equals("—")&&to.city.isEmpty())return null;
        return new FlightRoute(from,to);
    }
}
