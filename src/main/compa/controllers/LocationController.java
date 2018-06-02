package compa.controllers;

import com.google.gson.JsonElement;
import compa.exception.ParameterException;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import compa.app.Container;
import compa.models.Location;
import compa.app.Controller;
import compa.daos.LocationDAO;

import java.util.Date;
import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private LocationDAO locationDAO;

    public LocationController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "", this::newInstance, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "", this::getAll, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/getLocationsList/:startDate/:endDate", this::getLocationFromDateInterval, "application/json");

        locationDAO = (LocationDAO) container.getDAO(Location.class);
    }

    /**
     * @api {post} /location Declare a new position
     * @apiName PostLocation
     * @apiGroup Location
     *
     * @apiParam {double} latitude        Latitude of the postion
     * @apiParam {double} longitude       Longitude of the postion
     * @apiParam {datetime} datetime      The moment where the position was posted
     *
     * @apiSuccess Return the location
     */
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

    /**
     * @api {get} /location Get all location of the current user
     * @apiName GetLocations
     * @apiGroup Location
     *
     * @apiSuccess Return an array of locationDTO
     */
    private void getAll(User me, RoutingContext routingContext){
        locationDAO.getLocationFromUser(me,res -> {
            List<Location> list = res.result();
            routingContext.response().end(gson.toJson(locationDAO.toDTO(list)));
        });
    }

    /**
     * @api {get} /location/getLocationsList/:startDate/:endDate Get all location of the current user from a time interval
     * @apiName GetLocations
     * @apiGroup Location
     *
     *
     * @apiParam {datetime} startDate      Beginning
     * @apiParam {datetime} endDate        End
     *
     * @apiSuccess Return an array of locationDTO
     */
    private void getLocationFromDateInterval(User me, RoutingContext routingContext) {
        Date startDate, endDate;

        try {
            startDate = this.getParam(routingContext, "startDate", true, ParamMethod.GET, Date.class);
            endDate = this.getParam(routingContext, "endDate", true, ParamMethod.GET, Date.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        locationDAO.getLocationFromDateInterval(me,startDate,endDate,res -> {
            List<Location> locations = res.result();
            JsonElement tempEl = this.gson.toJsonTree(locationDAO.toDTO(locations));
            routingContext.response().end(gson.toJson(tempEl));
        });
    }

    /*private void getLocationFromDateInterval(User me, RoutingContext routingContext){

    }*/
}
