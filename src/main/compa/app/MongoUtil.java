package main.compa.app;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoUtil {

    private static final String DB_NAME = "compa";
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 27017;

    private Datastore datastore;

    public MongoUtil(String modelDirectory){
        final Morphia morphia = new Morphia();
        morphia.mapPackage(modelDirectory);
        datastore = morphia.createDatastore(new MongoClient(DB_HOST, DB_PORT), DB_NAME);
        datastore.ensureIndexes();
    }

    public Datastore getDatastore() {
        return datastore;
    }

}
