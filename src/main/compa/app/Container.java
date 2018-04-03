package main.compa.app;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class Container {

    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private Router router;
    private ModelManager modelManager;
    private MongoUtil mongoUtil;
    private List<Controller> controllers;

    public void run(ControllerFactory cf, DAOFactory daoFactory){
        Vertx vertx = Vertx.vertx();

        HttpServerOptions options = new HttpServerOptions();
        options.setHost(HOST);
        options.setPort(PORT);

        HttpServer server = vertx.createHttpServer(options);
        router = Router.router(vertx);
        mongoUtil = new MongoUtil();

        modelManager = new ModelManager(daoFactory.getDAOS(mongoUtil.getDatastore()));
        server.requestHandler(router::accept);
        server.listen();

        router.route().handler(BodyHandler.create());
        controllers = cf.getControllers(router, modelManager);

        //mongoUtil.getDatastore().save(new User("test", "test", null));
    }

    // TODO: Service manager
}
