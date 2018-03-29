package main.compa;

import io.vertx.core.json.JsonArray;

public class Location {
    private double latitude;
    private double longitude;
    public Location(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
    public JsonArray getJsonArray(){
        return new JsonArray().add(latitude).add(longitude);
    }
}
