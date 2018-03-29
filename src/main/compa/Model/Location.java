package main.compa.Model;

import io.vertx.core.json.JsonArray;
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

    public JsonArray getJsonArray(){
        return new JsonArray().add(latitude).add(longitude);
    }
}
