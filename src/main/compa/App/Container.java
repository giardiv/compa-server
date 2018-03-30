package main.compa.App;

import com.mongodb.MongoClient;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import main.compa.Controller.AuthenticationController;
import main.compa.Controller.LocationController;
import main.compa.Model.Location;
import main.compa.Model.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Container {

    private static Container instance = new Container();
    private Router router;
    private Datastore datastore;
    private ModelManager modelManager;

    private static final String MODEL_DIRECTORY = "main.compa.Model";
    private static final String DB_NAME = "compa";

    private Container(){}

    public static Container getInstance(){
        return instance;
    }

    public void run(){
        Container.getInstance().start();
    }

    public void start(){
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        router = Router.router(vertx);
        modelManager = new ModelManager();
        server.requestHandler(router::accept).listen(8080);
        router.route().handler(BodyHandler.create());

        final Morphia morphia = new Morphia();
        morphia.mapPackage(MODEL_DIRECTORY);
        datastore = morphia.createDatastore(new MongoClient(), DB_NAME);
        datastore.ensureIndexes();
        
        this.launchController();

        datastore.save(new User("test", "test"));
    }

    public void launchController(){
        LocationController locationController = new LocationController();
        AuthenticationController authenticationController = new AuthenticationController();
    }

    public Router getRouter(){
        return this.router;
    }

    public ModelManager getModelManager(){
        return this.modelManager;
    }

    public Datastore getDataStore(){
        return this.datastore;
    }
    // TODO: Service manager
}
