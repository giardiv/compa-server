package main.compa.daos;

import main.compa.App.Container;
import main.compa.Model.Location;
import main.compa.mongodb.AbstractDAO;

public class LocationDAO extends AbstractDAO {

    public LocationDAO(){
        super(Location.class, Container.getInstance().getDataStore());
    }

}
