package main.compa.App;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import main.compa.Controller.LocationController;

public class Container {

    private static Container instance = new Container();
    /*public JsonArray getJsonArray(){
    return new JsonArray().add(latitude).add(longitude);
}*/

    private Router router;

    private Container(){ }

    public static Container getInstance(){
        return instance;
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

        this.launchController();
    }

    public void launchController(){
        LocationController locationController = new LocationController();
    }

    public Router getRouter(){
        return this.router;
    }

    // TODO: Service manager
}
