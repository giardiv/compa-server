package main.compa.mongodb;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoUtil {

    private static final String MODEL_DIRECTORY = "main.compa.Model";
    private static final String DB_NAME = "compa";
    private static final String HOST = "localhost";
    private static final int PORT = 27017;

    private static Datastore datastore;

    public MongoUtil(){
        final Morphia morphia = new Morphia();
        morphia.mapPackage(MODEL_DIRECTORY);
        datastore = morphia.createDatastore(new MongoClient(HOST, PORT), DB_NAME);
        datastore.ensureIndexes();
    }

    public Datastore getDatastore() {
        return datastore;
    }

}
