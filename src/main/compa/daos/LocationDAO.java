package main.compa.daos;

import main.compa.Model.Location;
import main.compa.App.AbstractDAO;
import org.mongodb.morphia.Datastore;

public class LocationDAO extends AbstractDAO {

    public LocationDAO(Datastore ds){
        super(Location.class, ds);
    }

}
