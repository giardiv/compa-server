package compa.app;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoUtil {

    private static final String DB_NAME = "compa";
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 27017;

    private Datastore datastore;

    public MongoUtil(String modelDirectory, Container.MODE mode){
        final Morphia morphia = new Morphia();
        morphia.mapPackage(modelDirectory);
        String dbname = DB_NAME + (mode == Container.MODE.TEST ? "_test" : "");
        datastore = morphia.createDatastore(new MongoClient(DB_HOST, DB_PORT), dbname);
        datastore.ensureIndexes();
    }

    public Datastore getDatastore() {
        return datastore;
    }

}
