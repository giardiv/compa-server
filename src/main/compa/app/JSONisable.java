package main.compa.app;

import com.google.gson.*;
import main.compa.models.Location;
import main.compa.services.GsonService;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public interface JSONisable {
    default String toJSON(){
        return GsonService.getInstance().getGsonBuilder().create().toJson(this);
    }
}
