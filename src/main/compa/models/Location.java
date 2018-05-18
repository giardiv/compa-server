package main.compa.models;

import com.google.gson.annotations.Expose;
import main.compa.app.JSONisable;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity("location")
@Indexes({
        @Index(value = "latitude", fields = @Field("latitude")),
        @Index(value = "longitude", fields = @Field("longitude"))
})
public class Location implements JSONisable {
    static final long serialVersionUID = 42L;

    @Id
    @Expose
    private ObjectId id;

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    @Expose
    private Date time;

    public Location(){}

    public Location(double lat, double lng){
        this.latitude = lat;
        this.longitude = lng;
        this.time = new Date();
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

}
