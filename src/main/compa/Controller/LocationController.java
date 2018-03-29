package main.compa.Controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import main.compa.Location;
import main.compa.App.Controller;

import java.util.ArrayList;

public class LocationController extends Controller {
    private ArrayList<Location> locations = new ArrayList<Location>();

    private static final String PREFIX = "/location";

    public LocationController(){
        super(PREFIX);

        locations.add(new Location(1.1, 2.1));
        locations.add(new Location(1.2, 2.2));
        locations.add(new Location(1.3, 2.3));

        this.registerRoute(HttpMethod.PUT, null, this::handlePostLocation);
        this.registerRoute(HttpMethod.GET, null, this::handleGetAllLocation);
        this.registerRoute(HttpMethod.GET, "/:id", this::handleGetLocationById);
    }

    private void handlePostLocation(RoutingContext routingContext){

    }

    private void handleGetLocationById(RoutingContext routingContext){

    }

    private void handleGetAllLocation(RoutingContext routingContext){
        HttpServerResponse response = routingContext.response();
        response.write("route3");

        // Now end the response
        routingContext.response().end();

        //JsonArray arr = new JsonArray();
        //for (Location location: this.locations){arr.add(location.getJsonArray());}
        //routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }
}
