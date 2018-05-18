package main.compa.app;

import com.google.gson.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public interface JSONisable {
    default String toJSON(){
        GsonBuilder gb = new GsonBuilder();

        JsonSerializer<ObjectId> serializer = new JsonSerializer<ObjectId>() {
            public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonObj = new JsonObject();

                jsonObj.addProperty("_id", src.toString());

                return jsonObj;
            }
        };

        gb.registerTypeAdapter(ObjectId.class, serializer).setPrettyPrinting();

        return gb.excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }
}
