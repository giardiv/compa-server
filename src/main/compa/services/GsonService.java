package main.compa.services;

import com.google.gson.*;
import main.compa.app.Container;
import main.compa.app.Service;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class GsonService extends Service {

    GsonBuilder gb = new GsonBuilder();

    public GsonService(Container container) {
        super(container);

        JsonSerializer<ObjectId> serializer = new JsonSerializer<ObjectId>() {
            public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
                // #memories
                //JsonObject jsonObj = new JsonObject();
                //jsonObj.addProperty("value", src.toString());

                return new JsonPrimitive(src.toString());//.parse(src.toString();
            }
        };
                                                            // TODO: delete in prod
        gb.registerTypeAdapter(ObjectId.class, serializer).setPrettyPrinting();
        //gb.registerTypeAdapter(Location.class, new Adapter());
    }

    public GsonBuilder getGsonBuilder(){
        return this.gb;
    }
}
