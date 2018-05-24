package compa.services;

import com.google.gson.*;
import compa.app.Container;
import compa.app.Exception;
import compa.app.Service;
import compa.dtos.UserDTO;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class GsonService extends Service {

    Gson gson;

    public GsonService(Container container) {
        super(container);

        GsonBuilder gb = new  GsonBuilder().setPrettyPrinting();
        gb.setPrettyPrinting();

        // Exception Serializer
        JsonSerializer<Exception> exceptionSerializer = new JsonSerializer<Exception>() {
            @Override
            public JsonElement serialize(Exception e, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("message", e.getMessage());
                jsonObj.addProperty("code", e.getCode());
                return jsonObj;
            }
        };
        gb.registerTypeAdapter(Exception.class, exceptionSerializer);


        gson = gb.create();
    }

    public String toJson(Object thing){
        return gson.toJson(thing);
    }

    public JsonElement toJsonTree(Object thing){
        return gson.toJsonTree(thing);
    };
}
