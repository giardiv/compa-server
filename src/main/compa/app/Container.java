package main.compa.app;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class Container {

    private final static String SERVER_HOST = "localhost";
    private final static int SERVER_PORT = 8080;
    private Router router;
    private ModelManager modelManager;
    private MongoUtil mongoUtil;
    private List<Controller> controllers;

    public void run(ClassFinder cf){
        Vertx vertx = Vertx.vertx();

        HttpServerOptions options = new HttpServerOptions();
        options.setHost(SERVER_HOST);
        options.setPort(SERVER_PORT);

        HttpServer server = vertx.createHttpServer(options);
        router = Router.router(vertx);
        mongoUtil = new MongoUtil(cf.getModelDirectory());

        modelManager = new ModelManager(cf.getDAOs(mongoUtil.getDatastore()));
        server.requestHandler(router::accept);
        server.listen();
        // TODO: make it async 👉 https://github.com/vert-x3/vertx-examples/blob/master/core-examples/src/main/java/io/vertx/example/core/execblocking/ExecBlockingExample.java

        router.route().handler(BodyHandler.create());
        controllers = cf.getControllers(router, modelManager);

        //mongoUtil.getDatastore().save(new User("test", "test", null));
    }

    // TODO: Service manager
}
