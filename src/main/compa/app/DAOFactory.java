package main.compa.app;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.Map;

public interface DAOFactory {
    Map<Class, BasicDAO> getDAOS(Datastore ds);
}
