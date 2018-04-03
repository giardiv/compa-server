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
import main.compa.mongodb.MongoUtil;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Container {

    private static Container instance;
    private Router router;
    private ModelManager modelManager;
    private MongoUtil mongoUtil;

    private Container(){}

    public static Container getInstance(){
        if(instance == null)
            instance = new Container();
        return instance;
    }

    public void run(){
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        router = Router.router(vertx);
        modelManager = new ModelManager();
        server.requestHandler(router::accept).listen(8080);
        router.route().handler(BodyHandler.create());

        mongoUtil = new MongoUtil();
        this.launchController();

        //mongoUtil.getDatastore().save(new User("test", "test", null));
    }

    private void launchController(){
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
        return mongoUtil.getDatastore();
    }
    // TODO: Service manager
}
