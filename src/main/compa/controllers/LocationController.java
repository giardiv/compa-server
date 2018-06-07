package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import compa.daos.FriendshipDAO;
import compa.daos.LocationDAO;
import compa.daos.UserDAO;
import compa.exception.FriendshipException;
import compa.exception.LocationException;
import compa.exception.ParameterException;
import compa.exception.UserException;
import compa.models.Friendship;
import compa.models.Location;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    private static final int MAX_LOCATION = 100;

    private FriendshipDAO friendshipDAO;
    private LocationDAO locationDAO;
    private UserDAO userDAO;

    public LocationController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "", this::newInstance, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "", this::getAll, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/friend/:friend_id", this::getFromFriend, "application/json"); //OK
        this.registerAuthRoute(HttpMethod.GET, "/getLocationsList", this::getLastLocations, "application/json");//OK

        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        locationDAO = (LocationDAO) container.getDAO(Location.class);
        userDAO = (UserDAO) container.getDAO(User.class);
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
        locationDAO.getLocationsFromUser(me,res -> {
            List<Location> list = res.result().stream().limit(MAX_LOCATION).collect(Collectors.toList());
            routingContext.response().end(gson.toJson(locationDAO.toDTO(list)));
        });
    }

    /**
     * @api {get} /location/friend/:friend_id Get location of friend
     * @apiName getFromFriend
     * @apiGroup Location
     *
     * @apiSuccess Return an array of locationDTO
     */
    private void getFromFriend(User me, RoutingContext routingContext){
        final String friend_id;

        try {
            friend_id = this.getParam(routingContext, "friend_id", true, ParamMethod.GET, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(friend_id, res1 -> {
            User friend = res1.result();

            if(friend == null){
                routingContext.response().setStatusCode(404).end(gson.toJson(
                        new UserException(UserException.USER_NOT_FOUND, "id", friend_id)));
                return;
            }

            friendshipDAO.findFriendshipByUsers(me, friend, res2 -> {
                Friendship fs = res2.result();
                if(fs == null || (fs.getStatusA() != Friendship.Status.ACCEPTED
                        && fs.getStatusB() != Friendship.Status.ACCEPTED)){

                    routingContext.response().setStatusCode(400).end(gson.toJson(
                            new FriendshipException(FriendshipException.NOT_FRIEND)));
                    return;
                }

                if (friend.getGhostMode()) {
                    routingContext.response().setStatusCode(4040).end(gson.toJson(
                            new LocationException(LocationException.FRIEND_IS_GHOST))
                    );
                    return;
                }

                locationDAO.getLocationFromDateInterval(friend, res -> {
                    List<Location> list = res.result();
                    routingContext.response().end(gson.toJson(locationDAO.toDTO(list)));
                });
            });
        });
    }

    /**
     * @api {get} /location/getLocationsList Get all location of the current user from last 24h
     * @apiName GetLocations
     * @apiGroup Location
     *
     * @apiSuccess Return an array of locationDTO
     */
    private void getLastLocations(User me, RoutingContext routingContext) {

        locationDAO.getLocationFromDateInterval(me,res -> {
            List<Location> locations = res.result();
            routingContext.response().end(gson.toJson(locationDAO.toDTO(locations)));
        });
    }
}
