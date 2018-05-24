package compa.services;

import com.google.gson.*;
import compa.app.Container;
import compa.app.Exception;
import compa.app.Service;
import compa.dtos.UserDTO;
import compa.exception.LoginException;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class GsonService extends Service {

    Gson gson;

    public GsonService(Container container) {
        super(container);

        GsonBuilder gb = new  GsonBuilder();
        // Exception Serializer
        JsonSerializer<Exception> exceptionSerializer = (e, type, jsonSerializationContext) -> {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("code", e.getCode());
            jsonObj.addProperty("message", e.getMessage());
            return jsonObj;
        };

        for(Class<?> exceptionClass : container.getExceptions()){
            gb.registerTypeAdapter(exceptionClass, exceptionSerializer);
        }
        gb.setPrettyPrinting();


        gson = gb.create();
    }

    public String toJson(Object thing){
        return gson.toJson(thing);
    }

    public JsonElement toJsonTree(Object thing){
        return gson.toJsonTree(thing);
    };
}
