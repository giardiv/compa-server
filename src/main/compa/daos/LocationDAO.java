package main.compa.daos;

import main.compa.models.Location;
import main.compa.app.CustomDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

public class LocationDAO extends CustomDAO<Location, ObjectId> {

    public LocationDAO(Datastore ds){
        super(Location.class, ds);
    }

}
