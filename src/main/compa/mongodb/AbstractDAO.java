package main.compa.mongodb;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.List;

public abstract class AbstractDAO<T, K> extends BasicDAO<T, K> {

    public AbstractDAO(Class entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public List<T> findAll(){
        return this.find().asList();
    }
}
