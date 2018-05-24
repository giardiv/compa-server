package compa.controllers;

import com.google.gson.JsonElement;
import compa.app.Container;
import compa.app.Controller;
import compa.daos.UserDAO;
import compa.exception.ParameterException;
import compa.exception.UserException;
import compa.models.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class UserController extends Controller {
    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.GET, "/:id", this::getProfile, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "", this::getCurrentProfile, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "", this::updateProfile, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "/ghostmode", this::setGhostMode, "application/json");

        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {get} /user Get current user profile data
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiUse UserDTO
     */
    public void getCurrentProfile(User me, RoutingContext routingContext){
        final String status = routingContext.request().getParam("id");
        System.out.println(status);
        JsonElement tempEl = this.gson.toJsonTree(userDAO.toDTO(me));
        // todo add friendships.
        routingContext.response().end(gson.toJson(tempEl));
    }

    /**
     * @api {get} /user/:id Get the profile of the user with :id
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiUse UserDTO
     */
    public void getProfile(User me, RoutingContext routingContext){
        String id = null;
        try {
            id = (String) this.getParam(routingContext, "id", true, ParamMethod.GET, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        String finalId = id;
        userDAO.findById(id, res -> {
            User u = res.result();
            if(u != null){
                JsonElement tempEl = this.gson.toJsonTree(userDAO.toDTO(u));
                routingContext.response().end(gson.toJson(tempEl));
            } else {
                routingContext.response().setStatusCode(404).end(
                        gson.toJson(
                                new UserException(UserException.USER_NOT_FOUND, finalId)));
            }
        });
    }

    /**
     * @api {put} /user/ghostmode Set ghost mode
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiParam {Boolean} mode      If ghost mode is swith on/off
     *
     * @apiSuccess Return 200 without body
     */
    public void setGhostMode(User me, RoutingContext routingContext){
        boolean mode;
        try {
            mode = (boolean) this.getParam(routingContext, "mode", true, ParamMethod.JSON, Boolean.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.setGhostMode(me, mode, res -> {
            routingContext.response().end();
        });
    }

    public void updateProfile(User me, RoutingContext routingContext){
        // TODO
    }
}
