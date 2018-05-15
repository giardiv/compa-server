package main.compa.daos;

import main.compa.models.Location;
import main.compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

public class LocationDAO extends DAO<Location, ObjectId> {

    public LocationDAO(Datastore ds){
        super(Location.class, ds);
    }

}
