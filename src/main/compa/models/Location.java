package main.compa.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;


import java.util.Date;

@Entity("location")
@Indexes({
        @Index(value = "latitude", fields = @Field("latitude")),
        @Index(value = "longitude", fields = @Field("longitude"))
})
public class Location {

    @Id
    private ObjectId id;

    private double latitude, longitude;
    private Date datetime;

    public Location(){}

    public Location(double lat, double lng){
        this(lat, lng, null);
    }

    public Location(double lat, double lng, Date date){
        this.latitude = lat;
        this.longitude = lng;
        this.datetime = date;
    }

    public ObjectId getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getDatetime() {
        return datetime;
    }
}
