package main.compa.App;

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