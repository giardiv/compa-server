package main.compa.App;

import main.compa.Model.Location;
import main.compa.Model.User;
import main.compa.daos.LocationDAO;
import main.compa.daos.UserDAO;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {

    Map<Class, BasicDAO> daos;

    public ModelManager(Datastore ds){
        daos = new HashMap<>();
        daos.put(Location.class, new LocationDAO(ds));
        daos.put(User.class, new UserDAO(ds));
    }

    public BasicDAO getDAO(Class classs){
        return daos.get(classs);
    }

}
