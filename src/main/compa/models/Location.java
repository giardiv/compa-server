package compa.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;


import java.util.Date;

@Entity(value ="location", noClassnameStored = true)
@Indexes({
        @Index(value = "latitude", fields = @Field("latitude")),
        @Index(value = "longitude", fields = @Field("longitude"))
})
public class Location {

    @Id
    private ObjectId id;

    private String user_id;
    private double latitude, longitude;
    private Date datetime;

    public Location(){}

    public Location(double lat, double lng){
        this(null,lat, lng, null);
    }

    public Location(String user_id, double lat, double lng, Date date){
        this.user_id = user_id;
        this.latitude = lat;
        this.longitude = lng;
        this.datetime = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
