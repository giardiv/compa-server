package main.compa.daos;

import main.compa.models.Location;
import main.compa.app.AbstractDAO;
import org.mongodb.morphia.Datastore;

public class LocationDAO extends AbstractDAO {

    public LocationDAO(Datastore ds){
        super(Location.class, ds);
    }

}
