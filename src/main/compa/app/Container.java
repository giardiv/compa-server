package compa.app;
import io.netty.handler.ssl.OpenSsl;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.*;
import io.vertx.core.net.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import javax.security.auth.login.Configuration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Container {

    public final static String SERVER_HOST = "localhost";
    public final static int SERVER_PORT = 8080;
    private Router router;
    private Map<Class, DAO> daos;
    private MongoUtil mongoUtil;
    private Set<Class<?>> exceptions;
    private Map<Class, Service> services;
    private Vertx vertx;
    private MODE mode;

    private Handler<AsyncResult<HttpServer>> testHandler;

    public enum MODE {
        TEST,
        PROD
    }

    public Container(Handler<AsyncResult<HttpServer>> testHandler, MODE mode){
        this.testHandler = testHandler;
        this.mode = mode;
    }

    public void run(ClassFinder cf) {
        vertx = Vertx.vertx();

        HttpServerOptions options = new HttpServerOptions();
        options.setHost(SERVER_HOST);
        options.setPort(SERVER_PORT);
//        options.setSsl(true);
//        options.setKeyStoreOptions(
//                new JksOptions().setPath("C:\\Users\\amich\\.keystore").setPassword("compa2605"));
//

        HttpServer server = vertx.createHttpServer(options);


        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        mongoUtil = new MongoUtil(cf.getModelDirectory(), this.mode);

        daos = cf.getDAOs(this);
        exceptions = cf.getExceptions();
        services = cf.getServices(this);
        cf.getControllers(this);
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

    public Set<Class<?>> getExceptions() {
        return exceptions;
    }

    public Vertx getVertx() {
        return vertx;
    }
}
