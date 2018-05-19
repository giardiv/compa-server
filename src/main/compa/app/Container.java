package main.compa.app;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.mongodb.morphia.dao.BasicDAO;

import java.lang.Exception;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Container {

    private final static String SERVER_HOST = "localhost";
    private final static int SERVER_PORT = 8080;
    private Router router;
    private Map<Class, DAO> daos;
    private MongoUtil mongoUtil;
    private List<Controller> controllers;
    private Map<Class, Service> services;

    public void run(ClassFinder cf) {
        Vertx vertx = Vertx.vertx();

        HttpServerOptions options = new HttpServerOptions();
        options.setHost(SERVER_HOST);
        options.setPort(SERVER_PORT);

        HttpServer server = vertx.createHttpServer(options);
        router = Router.router(vertx);
        mongoUtil = new MongoUtil(cf.getModelDirectory());
        daos = cf.getDAOs(mongoUtil.getDatastore());

        server.requestHandler(router::accept);
        server.listen();
        // TODO: make it async 👉 https://github.com/vert-x3/vertx-examples/blob/master/core-examples/src/main/java/io/vertx/example/core/execblocking/ExecBlockingExample.java

        services = cf.getServices(this);
        router.route().handler(BodyHandler.create());
        controllers = cf.getControllers(this);
    }

    public Map<Class, Service> getServices() {
        return services;
    }

    public DAO getDAO(Class clazz){
        return daos.get(clazz);
    }

    public Router getRouter() {
        return router;
    }

    public MongoUtil getMongoUtil(){
        return mongoUtil;
    }

}
