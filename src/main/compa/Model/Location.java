package main.compa.Model;

import io.vertx.core.json.Json;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity("location")
@Indexes({
        @Index(value = "latitude", fields = @Field("latitude")),
        @Index(value = "longitude", fields = @Field("longitude"))
})
public class Location {
    @Id
    private ObjectId id;

    private double latitude;

    private double longitude;

    public Location(){}

    public Location(double lat, double lng){
        this.latitude = lat;
        this.longitude = lng;
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

    public String toJSON(){
        return Json.encodePrettily(this); //uses standard objectMapper
    }

}
