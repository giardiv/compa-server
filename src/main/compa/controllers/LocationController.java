package main.compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.ModelManager;
import main.compa.models.Location;
import main.compa.app.Controller;
import main.compa.daos.LocationDAO;
import org.bson.types.ObjectId;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private LocationDAO locationDAO;

    public LocationController(Router router, ModelManager modelManager){
        super(PREFIX, router);
        this.registerRoute(HttpMethod.POST, "/", this::newInstance, "application/json");
        this.registerRoute(HttpMethod.GET, "/", this::getAll, "application/json");
        this.registerRoute(HttpMethod.GET, "/:id", this::get, "application/json");

        locationDAO = (LocationDAO) modelManager.getDAO(Location.class);
    }

    private void newInstance(RoutingContext routingContext){

    }
    
    private void get(RoutingContext routingContext){
        String id = routingContext.request().getParam("id"); //if empty throw not found excep
        Location location = locationDAO.get(new ObjectId(id));
        routingContext.response().end(location.toJSON());
	}

    private void getAll(RoutingContext routingContext){
    	routingContext.response().end(new Gson().toJson(locationDAO.findAll()));
    }

}
