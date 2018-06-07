package compa.controllers;

import compa.app.Container;
import compa.app.Controller;
import compa.daos.FriendshipDAO;
import compa.daos.ImageDAO;
import compa.daos.UserDAO;
import compa.exception.*;
import compa.models.Friendship;
import compa.models.Image;
import compa.models.User;
import compa.services.ImageService;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
//?=nil user repos

public class UserController extends Controller {
    private static final String PREFIX = "/user";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    public UserController(Container container){
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.GET, "/:id", this::getProfile, "application/json");                   //OK
        this.registerAuthRoute(HttpMethod.GET, "", this::getCurrentProfile, "application/json");                //OK
        this.registerAuthRoute(HttpMethod.PUT, "/updateProfile", this::updateProfile, "application/json");
        this.registerAuthRoute(HttpMethod.PUT, "/ghostmode", this::setGhostMode, "application/json");
        this.registerAuthRoute(HttpMethod.POST, "/uploadPic", this::uploadPic, "application/json");

        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {get} /user Get current user profile data
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiUse UserDTO
     */
    private void getCurrentProfile(User me, RoutingContext routingContext){

        routingContext.response().end(gson.toJson(userDAO.toDTO(me)));
    }

    /**
     * @api {get} /user/:id Get the profile of the user with :id
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiUse UserDTO
     */
    private void getProfile(User me, RoutingContext routingContext){
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

                friendshipDAO.findFriendshipByUsers(me, u, res1 -> {
                    Friendship fs = res1.result();

                    if (fs == null || ( fs.getStatusA() != Friendship.Status.ACCEPTED && fs.getStatusB() != Friendship.Status.ACCEPTED)) {
                        routingContext.response().setStatusCode(404).end(gson.toJson(
                                new UserException(FriendshipException.NOT_FRIEND)));
                        return;
                    }

                    routingContext.response().end(gson.toJson(userDAO.toDTO(u)));
                });

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
    private void setGhostMode(User me, RoutingContext routingContext){
        boolean mode;
        try {
            mode = this.getParam(routingContext, "mode", true, ParamMethod.JSON, Boolean.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }

        userDAO.setGhostMode(me, mode, res -> routingContext.response().end("{}"));
    }

    /**
     * @api {put} /user/updateProfile Set name and email
     * @apiName GetMe
     * @apiGroup User
     *
     * @apiSuccess Return 200 without body
     */
    private void updateProfile(User me, RoutingContext routingContext){
        String name, email;
        try {
            email = this.getParam(routingContext, "email", true, ParamMethod.JSON, String.class);
            name = this.getParam(routingContext, "name", true, ParamMethod.JSON, String.class);
        } catch (ParameterException e) {
            routingContext.response().setStatusCode(400).end(gson.toJson(e));
            return;
        }
        if(!EmailValidator.getInstance(true).isValid(email)){
            routingContext.response().setStatusCode(400).end(gson.toJson(
                            new RegisterException(RegisterException.NOT_VALID_EMAIL)));
            return;
        }

        userDAO.findOne("email",email, res -> {
            User user = res.result();
            if(user != null){
                routingContext.response().setStatusCode(400).end(gson.toJson(new LoginException(UserException.USER_ALREADY_EXIST)));
                return;
            }

            userDAO.updateProfile(me, name,email, res1 -> {
                User u = res1.result();
                routingContext.response().end(gson.toJson(userDAO.toDTO(u)));
            });

        });


    }

    private void uploadPic(User me, RoutingContext routingContext){
      
        Set<FileUpload> files = routingContext.fileUploads();


        for(FileUpload file : files) {
            ImageService imageService = (ImageService) this.get(ImageService.class);
            imageService.upload(file, mapAsyncResult -> {
                if(mapAsyncResult.failed()){
                    routingContext.response().setStatusCode(400).end(gson.toJson(mapAsyncResult.cause()));
                } else {
                    Image image = mapAsyncResult.result();
                    userDAO.setProfilePic(me, image, res -> {
                        routingContext.response().setStatusCode(201).end(gson.toJson(userDAO.toDTO(res.result())));
                    });
                }
            });
            return;
        }
    }
}
