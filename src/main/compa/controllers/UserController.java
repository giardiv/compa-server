package compa.controllers;

import com.google.gson.JsonElement;
import compa.app.Container;
import compa.app.Controller;
import compa.daos.ImageDAO;
import compa.daos.UserDAO;
import compa.exception.LoginException;
import compa.exception.ParameterException;
import compa.exception.UserException;
import compa.models.Image;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.ImageService;
import io.vertx.core.Future;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

public class UserController extends Controller {
    private static final String PREFIX = "/user";

    private UserDAO userDAO;

    public UserController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.GET, "/:id", this::getProfile, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "", this::getCurrentProfile, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "", this::updateProfile, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "/ghostmode", this::setGhostMode, "application/json");
        this.registerAuthRoute(HttpMethod.POST, "/uploadPic", this::uploadPic, "application/json");

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
        JsonElement tempEl = this.gson.toJsonTree(userDAO.toDTO(me));
        // todo? add friendships
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
                                new UserException(UserException.USER_NOT_FOUND, "id", finalId)));
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

    public void uploadPic(User me, RoutingContext routingContext){
        String encryptedId = me.getId().toString();
        //new File("profile-images/" + encryptedId + ".png").delete();
        // Refresh

        Set<FileUpload> files = routingContext.fileUploads();

        for(FileUpload file : files) {
            File uploadedFile = new File(file.uploadedFileName());
            ImageService imageService = (ImageService) this.get(ImageService.class);
            imageService.upload(uploadedFile, mapAsyncResult -> {
                if(mapAsyncResult.failed()){
                    routingContext.response().setStatusCode(400).end(gson.toJson(mapAsyncResult.cause()));
                } else {
                    Image image = mapAsyncResult.result();
                    System.out.println(image);
                    new File(file.uploadedFileName()).delete();

                    routingContext.response().setStatusCode(201).end(gson.toJson(ImageDAO.toDTO(image)));
                    routingContext.response().close();
                }
            });
        }

    }
}
