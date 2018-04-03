package main.compa.app;

import main.compa.models.Location;
import main.compa.models.User;
import main.compa.daos.LocationDAO;
import main.compa.daos.UserDAO;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {

    Map<Class, BasicDAO> daos;

    public ModelManager(Datastore ds, DAOFactory df){
        daos = df.getDAOS(ds);
    }

    public BasicDAO getDAO(Class classs){
        return daos.get(classs);
    }

}
