package main.compa.App;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.mongodb.morphia.Datastore;

import java.util.ArrayList;

public class Controller {
    private String prefix;
    private ArrayList<Route> routes;

    public Controller(String prefix){
        this.prefix = prefix;
        this.routes = new ArrayList<Route>();
    }

    public Controller() {
        this.prefix = "";
    }

    protected void registerRoute(HttpMethod method, String route, Handler<RoutingContext> handler, String produces){
        routes.add(this.getRouter().route(method, prefix + route).produces(produces).handler(handler));
    }

    // TODO: Create service manager
    protected Router getRouter(){
        return Container.getInstance().getRouter();
    }

    protected ModelManager getManager(){
        return Container.getInstance().getModelManager();
    }

}
