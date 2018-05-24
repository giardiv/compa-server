package compa.daos;

import compa.dtos.FriendshipDTO;
import compa.models.Friendship;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.app.DAO;
import compa.dtos.UserDTO;
import compa.exception.FriendshipException;
import compa.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FriendshipDAO extends DAO<Friendship, ObjectId> {

    private Logger logger = Logger.getLogger("friendship_dao");

    public FriendshipDAO(Container container){
        super(Friendship.class, container);
    }

    public void findFriendshipsByStatus(User me, Friendship.Status m, Handler<AsyncResult<List<Friendship>>> resultHandler){
        vertx.executeBlocking( future -> {

        }, resultHandler);
    }

    public void deleteFriendship(Friendship friendship, Handler<AsyncResult<Boolean>> resultHandler){
        vertx.executeBlocking( future -> {

        }, resultHandler);
    }

    public void findFriendshipByUsers(User me, User friend, Handler<AsyncResult<Friendship>> resultHandler){
        vertx.executeBlocking( future -> {

        }, resultHandler);

    }

    public void updateFriendship(Friendship f, Friendship.Status m, Handler<AsyncResult<Friendship>> resultHandler){
        vertx.executeBlocking( future -> {

        }, resultHandler);
    }

    public void addFriendship(User a, User b, Handler<AsyncResult<Friendship>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",new Object[]{a.getLogin(), b.getLogin()});

            Friendship fs = new Friendship(a, b);
            this.save(fs);

            UpdateOperations<User> ops = getDatastore().createUpdateOperations(User.class).addToSet("friendships", fs);
            getDatastore().update(a, ops);
            getDatastore().update(b, ops);

            logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                    new Object[]{a.getLogin(), b.getLogin()});

            future.complete(fs);

        }, resultHandler);

    }

    public UserDTO toUserDTO(Friendship friendship){
        return new UserDTO(friendship.getMe());
    }

    public List<UserDTO> toUserDTO(List<Friendship> friendships){
        return friendships.stream().map(x -> new UserDTO(x.getMe())).collect(Collectors.toList());
    }

}