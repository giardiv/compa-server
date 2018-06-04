package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import compa.daos.LocationDAO;
import compa.exception.ParameterException;
import compa.models.Location;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";
    private static final int PERIOD = -5;

    private LocationDAO locationDAO;

    public LocationController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "", this::newInstance, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "", this::getAll, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/getLocationsList", this::getLocationFromDateInterval, "application/json");///:startDate/:endDate

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
            routingContext.response().end(gson.toJson(locationDAO.toDTO(locations)));
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
        //TODO ANOTHER ROUTE LIKE THIS ONE BUT
        // GET THE FRIEND_ID PARAM (GET IN THE URL)
        // AND THEN CHECK IF WE'RE FRIEND WITH THAT PERSON AND IF SO
        //CHECK THEIR GHOST MODE AND IF ITS TO FALSE THEN WE CAN RETURN THE LOCATIONS
        //SIMILAR TO GET PROFILE WITH THE :id

        locationDAO.getLocationsFromUser(me,res -> {
            //TODO RETURN ONLY A LIMITED NUMBER (EITHER A NUMBER OF LOCATIONS OR DEFINE A TIMEINTERVAL FROM CURRENT DATE
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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, PERIOD);
        endDate = calendar.getTime();

        locationDAO.getLocationFromDateInterval(me,startDate,endDate,res -> {
            List<Location> locations = res.result();
            routingContext.response().end(gson.toJson(locationDAO.toDTO(locations)));
        });
    }
}
