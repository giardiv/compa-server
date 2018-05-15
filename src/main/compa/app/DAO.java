package main.compa.app;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.List;

public class DAO<T, K> extends BasicDAO<T, K> {

    public DAO(Class entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public List<T> findAll(){
        return this.find().asList();
    }
}
