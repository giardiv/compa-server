package main.compa.App;

import main.compa.Model.Location;
import org.bson.types.ObjectId;

import java.util.List;

public class ModelManager {

    public Location findLocationById(String id){
        return Container.getInstance().getDataStore().get(Location.class, new ObjectId(id));
    }

    public List<Location> allLocations(){
        return Container.getInstance().getDataStore().find(Location.class).asList();
    }

}
