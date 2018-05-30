package compa.app;

import io.vertx.core.Vertx;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.List;
import java.util.Map;

public abstract class DAO<T, K> extends BasicDAO<T, K> {

    protected Vertx vertx;

    public DAO(Class entityClass, Container container) {
        super(entityClass, container.getMongoUtil().getDatastore());
        this.vertx = container.getVertx();
    }

    public List<T> findAll(){
        return this.find().asList();
    }

    public T findById(String id) {
        return this.findOne("id", new ObjectId(id));
    }

    public abstract void init(Map<Class, DAO> daos);
}
