package main.compa.App;

import main.compa.Model.Location;
import main.compa.Model.User;
import main.compa.daos.LocationDAO;
import main.compa.daos.UserDAO;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.HashMap;

public class ModelManager {

    HashMap<Class, BasicDAO> daos;

    public ModelManager(){
        daos = new HashMap<>();
        daos.put(Location.class, new LocationDAO());
        daos.put(User.class, new UserDAO());
    }

    public BasicDAO getDAO(Class classs){
        return daos.get(classs);
    }

}
