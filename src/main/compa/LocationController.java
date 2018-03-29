package main.compa;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;

public class LocationController {
    private ArrayList<Location> locations = new ArrayList<Location>();

    public LocationController(Router router){
        locations.add(new Location(1.1, 2.1));
        locations.add(new Location(1.2, 2.2));
        locations.add(new Location(1.3, 2.3));

        Route postLocation = router.put("/location").handler(this::handlePostLocation);
        Route getLocationById = router.get("/location/:id").handler(this::handleGetLocationById);
        Route getAllLocation = router.get("/location").handler(this::handleGetAllLocation);
    }

    private void handlePostLocation(RoutingContext routingContext){

    }

    private void handleGetLocationById(RoutingContext routingContext){

    }

    private void handleGetAllLocation(RoutingContext routingContext){
        JsonArray arr = new JsonArray();
        for (Location location: this.locations){arr.add(location.getJsonArray());}
        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }
}
