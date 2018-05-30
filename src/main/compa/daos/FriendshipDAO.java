package compa.daos;

import com.mongodb.operation.DeleteOperation;
import compa.dtos.FriendshipDTO;
import compa.models.Friendship;
import compa.models.Location;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.app.DAO;
import compa.dtos.UserDTO;
import compa.exception.FriendshipException;
import compa.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FriendshipDAO extends DAO<Friendship, ObjectId> {

    private Logger logger = Logger.getLogger("friendship_dao");

    public FriendshipDAO(Container container){
        super(Friendship.class, container);
    }

    public void findFriendsByStatus(User user, Friendship.Status status, Handler<AsyncResult<List<User>>> resultHandler){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for {0}'s friends", user.getLogin());
            Query<Friendship> query = this.createQuery();

            query.and(
                    query.criteria("friend").equal(user),
                    query.criteria("status").equal(status)
            );
            List<User> friendships = query.asList()
                    .stream()
                    .map(Friendship::getSister)
                    .map(Friendship::getFriend)
                    .collect(Collectors.toList());
            logger.log(Level.INFO, "Found {0} friends", friendships.size());

            future.complete(friendships);

        }, resultHandler);
    }

    public void findFriendshipByUsers(User me, User friend, Handler<AsyncResult<Friendship>> resultHandler){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for {0} 's friends", me.getLogin());
            QueryResults<Friendship> friendsFriends = this.find(this.createQuery().field("friend").equal(friend));
            Query<Friendship> query = this.createQuery();
            query.and(
                    query.criteria("friend").equal(me),
                    query.criteria("sister").in(friendsFriends.asList())
            );
            Friendship friendship = this.findOne(query);
            logger.log(Level.INFO, "Found {0} friends", friendship);

            future.complete(friendship);

        }, resultHandler);
    }

    public void addFriendship(User me, User friend, Handler<AsyncResult<Friendship>> resultHandler) {
     vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",new Object[]{me.getLogin(), friend.getLogin()});
            Friendship fs = new Friendship(me, friend);
            this.save(fs);
            this.save(fs.getSister());
            logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                    new Object[]{me.getLogin(), friend.getLogin()});
            future.complete(fs);
        }, resultHandler);
    }

    public void updateFriendship(Friendship f, Friendship.Status m, Handler<AsyncResult<Friendship>> resultHandler){
        vertx.executeBlocking( future -> {
            // TODO: updateOperations ?
            f.setStatus(m);
            this.save(f);
            f.getSister().setStatus(Friendship.getRiprocalStatus(m));
            this.save(f.getSister());
            System.out.println(f.getSister().getStatus());
            System.out.println(f.getStatus());
            System.out.println(m);
            future.complete(f);
        }, resultHandler);
    }

    public void deleteFriendship(Friendship fs, Handler<AsyncResult<Boolean>> resultHandler){
        vertx.executeBlocking( future -> {
            this.getDatastore().delete(fs.getSister());
            this.getDatastore().delete(fs);
            future.complete();
        }, resultHandler);
    }

    public UserDTO toUserDTO(Friendship friendship) {
        return new UserDTO(friendship.getFriend());
    }

    public List<UserDTO> toUserDTO(List<Friendship> friendships){
        return friendships.stream().map(x -> new UserDTO(x.getFriend())).collect(Collectors.toList());

    }

    public List<FriendshipDTO> toDTO(List<Friendship> friendships){
        return friendships.stream().map(FriendshipDTO::new).collect(Collectors.toList());
    }

    @Override
    public void init(Map<Class, DAO> daos) {

    }
}