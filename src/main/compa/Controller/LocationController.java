package main.compa.Controller;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonArray;
import main.compa.App.Container;
import main.compa.App.ModelManager;
import main.compa.Model.Location;
import main.compa.App.Controller;
import main.compa.daos.LocationDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private LocationDAO locationDAO;

    public LocationController(){
        super(PREFIX);
        this.registerRoute(HttpMethod.POST, "/", this::newInstance, "application/json");
        this.registerRoute(HttpMethod.GET, "/", this::getAll, "application/json");
        this.registerRoute(HttpMethod.GET, "/:id", this::get, "application/json");

        locationDAO = (LocationDAO) Container.getInstance().getModelManager().getDAO(Location.class);
    }

    private void newInstance(RoutingContext routingContext){

    }
    
    private void get(RoutingContext routingContext){
        String id = routingContext.request().getParam("id"); //if empty throw not found excep
        Location location = (Location) locationDAO.get(new ObjectId(id));
        routingContext.response().end(new Gson().toJson(location));
	}

    private void getAll(RoutingContext routingContext){
    	routingContext.response().end(new Gson().toJson(locationDAO.findAll()));
    }

   
}
