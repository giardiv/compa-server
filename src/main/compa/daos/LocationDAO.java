package main.compa.daos;

import main.compa.models.Location;
import main.compa.app.CustomDAO;
import org.mongodb.morphia.Datastore;

public class LocationDAO extends CustomDAO {

    public LocationDAO(Datastore ds){
        super(Location.class, ds);
    }

}
