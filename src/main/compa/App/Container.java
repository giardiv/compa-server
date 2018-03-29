package main.compa.App;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import main.compa.Controller.LocationController;

public class Container extends AbstractVerticle{

    private static Container INSTANCE = new Container();

    private Router router;

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

        this.lunchController();
    }

    public void lunchController(){
        LocationController locationController = new LocationController();
    }

    public Router getRouter(){
        return this.router;
    }

    // TODO: Service manager
}
