package com.nearsky.tv;

import java.util.*;

/** Pick nearest complete flights while bounding enrichment work, not just the first three positions. */
final class FlightSelection {
    enum State { COMPLETE, PENDING, UNAVAILABLE }
    interface Resolver { State resolve(Flight flight,boolean allowLookup); }
    static final class Result {
        final List<Flight> flights;final boolean pending;
        Result(List<Flight> flights,boolean pending){this.flights=flights;this.pending=pending;}
    }
    static Result select(List<Flight> ordered,Resolver resolver){
        List<Flight> visible=new ArrayList<>();int pending=0;
        for(Flight flight:ordered){
            State state=resolver.resolve(flight,pending<6);
            if(state==State.COMPLETE){visible.add(flight);if(visible.size()==3)break;}
            else if(state==State.PENDING)pending++;
        }
        return new Result(visible,pending>0);
    }
    static boolean known(String value){
        String s=AirlineNames.clean(value).toLowerCase(Locale.ROOT);
        return !s.isEmpty()&&!s.equals("—")&&!s.equals("-")&&!s.equals("n/a")&&!s.equals("null")
            &&!s.contains("unknown")&&!s.contains("unavailable");
    }
    static boolean baseComplete(Flight f){
        return !LiveryDesign.fromCallsign(f.callsign).isEmpty()&&!FlightRoute.callsign(f.callsign).isEmpty()
            &&known(f.registration)&&(known(f.description)||known(f.type))
            &&Double.isFinite(f.altitudeFt)&&Double.isFinite(f.speedKnots)&&f.speedKnots>=0
            &&Double.isFinite(f.heading)&&f.heading>=0&&f.heading<=360;
    }
    static boolean complete(Flight f,String airline,String country,FlightRoute route){
        return baseComplete(f)&&known(airline)&&!airline.equalsIgnoreCase(LiveryDesign.fromCallsign(f.callsign))&&known(country)
            &&route!=null&&!route.classification().equals("ROUTE UNKNOWN")
            &&known(route.origin.code)&&known(route.origin.city)&&known(route.destination.code)&&known(route.destination.city);
    }
    static String model(Flight f,String metadata){
        return known(metadata)?metadata:known(f.description)?f.description:known(f.type)?f.type:"";
    }
}
