package main.compa.App;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import main.compa.Controller.UserController;
import main.compa.Controller.LocationController;

import java.util.ArrayList;
import java.util.List;

public class Container {

    private Router router;
    private ModelManager modelManager;
    private MongoUtil mongoUtil;
    private List<Controller> controllers;

    public void run(){
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        router = Router.router(vertx);
        mongoUtil = new MongoUtil();
        modelManager = new ModelManager(mongoUtil.getDatastore());
        server.requestHandler(router::accept).listen(8080);
        router.route().handler(BodyHandler.create());

        controllers = new ArrayList<>();
        controllers.add(new LocationController(router, modelManager));
        controllers.add( new UserController(router, modelManager));

        //mongoUtil.getDatastore().save(new User("test", "test", null));
    }

    // TODO: Service manager
}
