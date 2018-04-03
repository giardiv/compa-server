package main.compa.app;

import org.mongodb.morphia.dao.BasicDAO;
import java.util.Map;

public class ModelManager {

    Map<Class, BasicDAO> daos;

    public ModelManager( Map<Class, BasicDAO> daos){
        this.daos = daos;
    }

    public BasicDAO getDAO(Class classs){
        return daos.get(classs);
    }

}
