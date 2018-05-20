package main.compa.controllers;

import io.vertx.core.json.JsonArray;
import main.compa.app.Container;
import main.compa.daos.FriendshipDAO;
import main.compa.daos.UserDAO;
import main.compa.dtos.UserDTO;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import main.compa.app.Controller;
import main.compa.models.Friendship;
import main.compa.models.User;
import main.compa.services.GsonService;

import java.util.List;

public class FriendshipController extends Controller {
    private static final String PREFIX = "/friendship";

    private FriendshipDAO friendshipDAO;
    private UserDAO userDAO;

    //TODO PROBABLY RENAME THE USER VARIABLE NAMES IN THE FRIENDSHIP TO INDICATE WHO ASKED WHO
    //FOR NOW : friendleft = the one who asked

    public FriendshipController(Container container) {
        super(PREFIX, container);
        this.registerAuthRoute(HttpMethod.POST, "/request", this::requestFriendship, "application/json");
        this.registerAuthRoute(HttpMethod.POST, "/change", this::editFriendship, "application/json"); //PUT?
        this.registerAuthRoute(HttpMethod.GET, "/friend_list", this::getFriends, "application/json");
        this.registerAuthRoute(HttpMethod.GET, "/pending", this::getPendingFriendships, "application/json");

        friendshipDAO = (FriendshipDAO) container.getDAO(Friendship.class);
        userDAO = (UserDAO) container.getDAO(User.class);
    }

    /**
     * @api {post} /friendship/request Add a new friendship
     * @apiName Request Friendship
     * @apiGroup Friendship
     * @apiParam {String} friend_id : the id of the user you want to become friends with
     * @apiUse FriendshipAlreadyExist
     */
    private void requestFriendship(User me, RoutingContext routingContext) {
        GsonService gson = (GsonService) this.get(GsonService.class);

        if(!this.checkParams(routingContext, "friend_id")){
            routingContext.response().end("missing parameter"); //TODO FORMAT
            return;
        }

        userDAO.findById(routingContext.request().getParam("friend_id"), res1 -> {

            User friend = res1.result();

            if(friend == null){
                routingContext.response().end("can't find your friend"); //TODO FORMAT
                return;
            }

            if(friend.equals(me)){
                routingContext.response().end("you can't befriend yourself"); //TODO FORMAT
                return;
            }

            friendshipDAO.addFriendship(me, friend, res2 -> {
                if(res2.failed()){
                    routingContext.response().setStatusCode(418).end(gson.toJson(res2.cause()));
                }
                else{
                    routingContext.response().end("you are now friends"); //TODO FORMAT
                }
            });
        });


    }

    /**
     * @api {get} /friends Get the friends of the user
     * @apiName GetFriendship
     * @apiGroup Friendship
     * @apiParam {String} user_id : id of user whose friend list is request
     */
    private void getFriends(User me, RoutingContext routingContext){

        GsonService gson = (GsonService) this.get(GsonService.class);

        if(!this.checkParams(routingContext, "user_id")) {
            routingContext.response().end("missing param"); //TODO FORMAT
            return;
        }

        userDAO.findById(routingContext.request().getParam("user_id"), res -> {
            User other = res.result();
            if(other == null){
                routingContext.response().end("can't find user whose friend list is requested"); //TODO FORMAT
                return;
            }

            friendshipDAO.getFriendshipByFriends(me, other, res2 -> {
                Friendship friendship = res2.result();

                if(friendship == null || friendship.getStatus() != Friendship.Status.ACCEPTED){
                    routingContext.response().end("can't see this user's friends : you aren't friends"); //TODO FORMAT
                    return;
                }
                else {

                    friendshipDAO.getFriendshipsByUser(other, res3 -> {
                        List<Friendship> friendships = res3.result();
                        List<UserDTO> friends = friendshipDAO.toDTO(friendships, other);
                        routingContext.response().end(gson.toJson(friends));
                        return;
                    }); //also works if me.equals(other)
                }
            });

        });

    }

    private void editFriendship(User me, RoutingContext routingContext){
        GsonService gson = (GsonService) this.get(GsonService.class);

        if(!this.checkParams(routingContext, "friendship_id", "status")){
            routingContext.response().end("missing parameter"); //TODO FORMAT
            return;
        }


    }

    private void getPendingFriendships(User me, RoutingContext routingContext){
        friendshipDAO.getPendingFriendships(me, res -> {
            List<Friendship> friendships = res.result();
        });
    }

}
