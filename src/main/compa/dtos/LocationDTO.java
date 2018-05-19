package main.compa.dtos;

import main.compa.app.JSONisable;
import main.compa.models.Location;

import java.time.LocalDateTime;

public class LocationDTO implements JSONisable {

    private double latitude, longitude;
    private LocalDateTime datetime;

    public LocationDTO(Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.datetime = new java.sql.Timestamp(
                location.getDatetime().getTime()).toLocalDateTime();
    }
}
