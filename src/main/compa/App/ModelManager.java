package main.compa.App;

import main.compa.Model.Location;
import org.bson.types.ObjectId;

public class ModelManager {

    public static Location findLocationById(String id){
        return Container.getInstance().getDataStore().get(Location.class, new ObjectId(id));
    }
}
