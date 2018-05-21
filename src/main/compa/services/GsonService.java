package compa.services;

import com.google.gson.*;
import compa.app.Container;
import compa.app.Service;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class GsonService extends Service {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonService(Container container) {
        super(container);
    }

    public String toJson(Object thing){
        return gson.toJson(thing);
    }
}
