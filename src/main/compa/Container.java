package main.compa;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

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
        LocationController locationController = new LocationController(this.router);
    }

    // TODO: Service manager
}
