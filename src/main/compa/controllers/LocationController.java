package compa.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import compa.exception.ParameterException;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import compa.app.Container;
import compa.dtos.LocationDTO;
import compa.models.Location;
import compa.app.Controller;
import compa.daos.LocationDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private LocationDAO locationDAO;

    public LocationController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "/", this::newInstance, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/", this::getAll, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/getLocationsList", this::getLocationFromDateInterval, "application/json");

        locationDAO = (LocationDAO) container.getDAO(Location.class);
    }

    private void newInstance(User me, RoutingContext routingContext){
        Double latitude, longitude;
        Date date;
        try {
            latitude = this.getParam(routingContext, "latitude", true, ParamMethod.JSON, Double.class);
            longitude = this.getParam(routingContext, "longitude", true, ParamMethod.JSON, Double.class);
            date = this.getParam(routingContext, "datetime", true, ParamMethod.JSON, Date.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }
        locationDAO.addPosition(me,latitude,longitude,date,res -> {
            Location locations = res.result();
            JsonElement tempEl = this.gson.toJsonTree(locationDAO.toDTO(locations));
            routingContext.response().end(gson.toJson(tempEl));
        });
    }

    private void getAll(User me, RoutingContext routingContext){
        List<LocationDTO> list = locationDAO.toDTO(locationDAO.findAll());
    	routingContext.response().end(new Gson().toJson(list));
    }

    private void getLocationFromDateInterval(User me, RoutingContext routingContext) {
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("25-05-2018 17:09:35");
            endDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("25-05-2018 17:12:55");
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        try {
//            start = this.getParam(routingContext, "startDate", true, ParamMethod.JSON, Date.class);
//            endDate = this.getParam(routingContext, "endDate", true, ParamMethod.JSON, Date.class);
//        } catch (ParameterException e) {
//            routingContext.response().setStatusCode(400).end(gson.toJson(e));
//            return;
//        }


        locationDAO.getLocationFromDateInterval(me,startDate,endDate,res -> {
            List<Location> locations = res.result();
            System.out.println(locations);
            JsonElement tempEl = this.gson.toJsonTree(locationDAO.toDTO(locations));
            routingContext.response().end(gson.toJson(tempEl));

        });
    }
}
