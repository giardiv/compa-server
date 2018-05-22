package compa.app;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;
import java.util.Map;

public class Container {

    private final static String SERVER_HOST = "localhost";
    private final static int SERVER_PORT = 8080;
    private Router router;
    private Map<Class, DAO> daos;
    private MongoUtil mongoUtil;
    private List<Controller> controllers;
    private Map<Class, Service> services;
    private Vertx vertx;

    private Handler<AsyncResult<HttpServer>> testHandler;

    public Container(Handler<AsyncResult<HttpServer>> testHandler){
        this.testHandler = testHandler;
    }

    public void run(ClassFinder cf) {
        vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        options.setHost(SERVER_HOST);
        options.setPort(SERVER_PORT);

        HttpServer server = vertx.createHttpServer(options);
        router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        mongoUtil = new MongoUtil(cf.getModelDirectory());

        daos = cf.getDAOs(this);
        controllers = cf.getControllers(this);
        services = cf.getServices(this);

        server.requestHandler(router::accept);

        if(testHandler != null)
            server.listen(testHandler);
        else
            server.listen();

        //TODO CHANGE THIS, ONLY FOR TESTING

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

    public Vertx getVertx() {
        return vertx;
    }
}
