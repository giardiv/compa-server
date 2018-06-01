package compa.controllers;

import com.google.gson.JsonElement;
import com.mongodb.gridfs.GridFS;
import compa.app.Container;
import compa.app.Controller;
import compa.daos.ImageDAO;
import compa.daos.UserDAO;
import compa.exception.LoginException;
import compa.exception.ParameterException;
import compa.exception.RegisterException;
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
import org.apache.commons.validator.routines.EmailValidator;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileInputStream;
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
        this.registerAuthRoute(HttpMethod.PUT, "/updateProfile", this::updateProfile, "application/json");
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
        final String id;

        try {
            id = this.getParam(routingContext, "id", true, ParamMethod.GET, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.findById(id, res -> {
            User u = res.result();
            if(u != null){
                routingContext.response().end(gson.toJson(userDAO.toDTO(u)));
            } else {
                routingContext.response().setStatusCode(404).end(gson.toJson(
                                new UserException(UserException.USER_NOT_FOUND, "id", id)));
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
            mode = this.getParam(routingContext, "mode", true, ParamMethod.JSON, Boolean.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.setGhostMode(me, mode, res -> {
            routingContext.response().end(gson.toJson("{\"success\":true}"));
        });
    }

    /**
     * @api {put} /user/updateProfile Set name and email
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiSuccess Return 200 without body
     */
    public void updateProfile(User me, RoutingContext routingContext){
        String name = null;
        String email = null;
        try {
            email = this.getParam(routingContext, "email", true, ParamMethod.JSON, String.class);
            name = this.getParam(routingContext, "name", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }
        if(!EmailValidator.getInstance(true).isValid(email)){
            routingContext.response().setStatusCode(400).end(
                    gson.toJson(
                            new RegisterException(RegisterException.NOT_VALID_EMAIL)));
            return;
        }
        userDAO.updateProfile(me, name,email, res -> {
            User u = res.result();
            JsonElement tempEl = this.gson.toJsonTree(userDAO.toDTO(u));

            routingContext.response().end(gson.toJson(tempEl));
        });
    }

    public byte[] LoadImage(String filePath) throws Exception {
            File file = new File(filePath);
            int size = (int) file.length();
            byte[] buffer = new byte[size];
            FileInputStream in = new FileInputStream(file);
            in.read(buffer);
            in.close();
            return buffer;

    }


    public void uploadPic(User me, RoutingContext routingContext){

        String encryptedId = me.getId().toString(); //why encryptedid? why not just id?
        //new File("profile-images/" + encryptedId + ".png").delete();
        // Refresh
        //TODO SURROUND WITH VERTX BLOCKING AS IT MIGHT BE TIME CONSUMING???
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
