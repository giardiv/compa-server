package main.compa.App;

import com.mongodb.MongoClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import main.compa.Controller.LocationController;
import main.compa.Model.Location;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Container extends AbstractVerticle{

    private static Container INSTANCE = new Container();

    private Router router;
    private Datastore datastore;
    private ModelManager modelManager;

    private Container(){ }

    public static Container getInstance(){
        return INSTANCE;
    }

    public void run(){
        Container.getInstance().start();
    }

    public void start(){
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        this.router = Router.router(vertx);

        server.requestHandler(router::accept).listen(8080);
        router.route().handler(BodyHandler.create());

        final Morphia morphia = new Morphia();
        morphia.mapPackage("main.compa.Model");
        datastore = morphia.createDatastore(new MongoClient(), "compa");
        datastore.ensureIndexes();

        // Test adding
        datastore.save(new Location());

        this.lunchController();
    }

    public void lunchController(){
        LocationController locationController = new LocationController();
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
