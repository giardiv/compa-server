package main.compa.services;

import com.google.gson.*;
import main.compa.app.Adapter;
import main.compa.app.Service;
import main.compa.models.Location;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class GsonService implements Service {
    GsonBuilder gb = new GsonBuilder();

    public GsonService() {
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

    /** Instance unique pré-initialisée */
    private static GsonService INSTANCE = new GsonService();

    /** Point d'accès pour l'instance unique du singleton */
    public static GsonService getInstance()
    {
        return INSTANCE;
    }

    public GsonBuilder getGsonBuilder(){
        return this.gb;
    }
}
