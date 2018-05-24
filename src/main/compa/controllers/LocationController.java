package compa.controllers;

import com.google.gson.Gson;
import compa.exception.ParameterException;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import compa.app.Container;
import compa.dtos.LocationDTO;
import compa.models.Location;
import compa.app.Controller;
import compa.daos.LocationDAO;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private LocationDAO locationDAO;

    public LocationController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "/", this::newInstance, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/", this::getAll, "application/json");

        locationDAO = (LocationDAO) container.getDAO(Location.class);
    }

    private void newInstance(User me, RoutingContext routingContext){
        try {
            Double latitude = this.getParam(routingContext, "latitude", true, ParamMethod.JSON, Double.class);
            Double longitude = this.getParam(routingContext, "longitude", true, ParamMethod.JSON, Double.class);
            Date date = this.getParam(routingContext, "datetime", true, ParamMethod.JSON, Date.class);

        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }
    }

    private void getAll(User me, RoutingContext routingContext){
        List<LocationDTO> list = locationDAO.toDTO(locationDAO.findAll());
    	routingContext.response().end(new Gson().toJson(list));
    }
}
