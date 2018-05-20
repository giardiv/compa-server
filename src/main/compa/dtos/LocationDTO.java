package main.compa.dtos;

import main.compa.models.Location;

import java.text.SimpleDateFormat;

public class LocationDTO {

    private double latitude, longitude;
    private String datetime;

    public LocationDTO(Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.datetime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(location.getDatetime());
    }
}
