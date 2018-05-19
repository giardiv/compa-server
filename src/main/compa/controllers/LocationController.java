package main.compa.controllers;

import com.google.gson.Gson;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Container;
import main.compa.dtos.LocationDTO;
import main.compa.models.Location;
import main.compa.app.Controller;
import main.compa.daos.LocationDAO;
import org.bson.types.ObjectId;

import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private LocationDAO locationDAO;

    public LocationController(Container container){
        super(PREFIX, container);
        this.registerRoute(HttpMethod.POST, "/", this::newInstance, "application/json");
        this.registerRoute(HttpMethod.GET, "/", this::getAll, "application/json");
        this.registerRoute(HttpMethod.GET, "/:id", this::get, "application/json");

        locationDAO = (LocationDAO) container.getDAO(Location.class);
    }

    private void newInstance(RoutingContext routingContext){

    }
    
    private void get(RoutingContext routingContext){
        String id = routingContext.request().getParam("id"); //if empty throw not found excep
        Location location = locationDAO.get(new ObjectId(id));
        routingContext.response().end(new Gson().toJson(locationDAO.toDTO(location)));
	}

    private void getAll(RoutingContext routingContext){
        List<LocationDTO> list = locationDAO.toDTO(locationDAO.findAll());
    	routingContext.response().end(new Gson().toJson(list));
    }
}
