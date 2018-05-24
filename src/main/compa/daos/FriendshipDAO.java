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

    public void addFriendship(User friend,User me, Handler<AsyncResult<Friendship>> resultHandler) {
     vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",new Object[]{me.getLogin(), friend.getLogin()});

            Friendship fs_me = new Friendship(me);
            Friendship fs_friend = new Friendship(friend);
            this.save(fs_friend);
            fs_me.setSister(fs_friend);
            this.save(fs_me);
            fs_friend.setSister(fs_me);
            UpdateOperations<Friendship> ops = getDatastore().createUpdateOperations(Friendship.class).addToSet("sister",fs_me );
            getDatastore().update(fs_friend, ops);

            logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                    new Object[]{me.getLogin(), friend.getLogin()});

            future.complete(fs_me);

        }, resultHandler);

    }

    public void findFriendshipsByStatus(User me, Friendship.Status m, Handler<AsyncResult<List<Friendship>>> resultHandler){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for {0}'s friends", me.getLogin());
            Query<Friendship> query = this.createQuery();
            query.and(
                    query.criteria("friend").equal(me),
                    query.criteria("status").equal(m)
            );
            query.project("sister",true).asList();
            List<Friendship> friendships = this.find(query).asList();
            logger.log(Level.INFO, "Found {0} friends", friendships.size());

            future.complete(friendships);

        }, resultHandler);
    }

    public void deleteFriendship(Friendship friendship, Handler<AsyncResult<Boolean>> resultHandler){
        vertx.executeBlocking( future -> {

        }, resultHandler);
    }

    public void findFriendshipByUsers(User me, User friend, Handler<AsyncResult<List<Friendship>>> resultHandler){

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for {0}'s friends", me.getLogin());
            Query<Friendship> query = this.createQuery();
            query.or(
                    query.criteria("friend").equal(me)
            );
            query.project("sister",true).asList();
            List<Friendship> friendships = this.find(query).asList();
            logger.log(Level.INFO, "Found {0} friends", friendships.size());

            future.complete(friendships);

        }, resultHandler);

    }

    public void updateFriendship(Friendship f, Friendship.Status m, Handler<AsyncResult<Friendship>> resultHandler){
        vertx.executeBlocking( future -> {

        }, resultHandler);

    }

    public UserDTO toUserDTO(Friendship friendship) {
        return new UserDTO(friendship.getFriend());
    }

    public List<UserDTO> toUserDTO(List<Friendship> friendships){
        return friendships.stream().map(x -> new UserDTO(x.getFriend())).collect(Collectors.toList());

    }

}