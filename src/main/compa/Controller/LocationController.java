package main.compa.Controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import main.compa.Model.Location;
import main.compa.App.Controller;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class LocationController extends Controller {
    private ArrayList<Location> locations = new ArrayList<Location>();

    private static final String PREFIX = "/location";

    public LocationController(){
        super(PREFIX);

        this.registerRoute(HttpMethod.PUT, null, this::handlePostLocation);
        this.registerRoute(HttpMethod.GET, null, this::handleGetAllLocation);
        this.registerRoute(HttpMethod.GET, "/:id", this::handleGetLocationById);
    }

    private void handlePostLocation(RoutingContext routingContext){

    }

    private void handleGetLocationById(RoutingContext routingContext){

    }

    private void handleGetAllLocation(RoutingContext routingContext){
        JsonArray arr = new JsonArray();
        final Query<Location> query = this.getDataStore().createQuery(Location.class);
        final List<Location> locations = query.asList();
        for (Location location: locations){arr.add(location.getJsonArray());}
        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }
}
