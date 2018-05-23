package compa.daos;

import compa.dtos.FriendshipDTO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.app.DAO;
import compa.dtos.UserDTO;
import compa.exception.FriendshipException;
import compa.models.Friendship;
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

    /**
     *
     * @param status
     * @param user
     * @param resultHandler
     */
    public void getFriendshipsByUser(String status, User user, Handler<AsyncResult<List<Friendship>>> resultHandler){

        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Looking for {0}'s friends", user.getLogin());
            Query<Friendship> query = this.createQuery();
            query.or(
                    query.criteria("userAsker").equal(user),
                    query.criteria("userAsked").equal(user)
            );
            if(status!=null){
                query.or(query.criteria("status").equal(status));
            }

            List<Friendship> friendships = this.find(query).asList();
            logger.log(Level.INFO, "Found {0} friends", friendships.size());
            future.complete(friendships);

        }, resultHandler);

    }

    public void getPendingFriendships(User me, Handler<AsyncResult<List<Friendship>>> resultHandler){

        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Looking for {0}'s pending friendships", me.getLogin());
            Query<Friendship> query = this.createQuery();
            query.and(
                    query.or(
                            query.criteria("userAsker").equal(me),
                            query.criteria("userAsked").equal(me)
                    ),
                    query.criteria("status").equal(Friendship.Status.PENDING)
            );

            List<Friendship> friendships = this.find(query).asList();
            logger.log(Level.INFO, "Found {0} friendship requests", friendships.size());
            future.complete(friendships);

        }, resultHandler);

    }

    public void addFriendship(User a, User b, Handler<AsyncResult<Friendship>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",
                    new Object[]{a.getLogin(), b.getLogin()});

            this.getFriendshipByFriends(a, b, res -> {
                Friendship fs = res.result();

                if(fs != null) {
                    logger.log(Level.WARNING, "Friendship between {0} and {1} already exists",
                            new Object[]{a.getLogin(), b.getLogin()});
                    future.fail(new FriendshipException(FriendshipException.FRIEND_ALREADY_EXIST));
                }

                fs = new Friendship(a, b);
                this.save(fs);

                UpdateOperations<User> ops = getDatastore().createUpdateOperations(User.class).addToSet("friendships", fs);
                getDatastore().update(a, ops);
                getDatastore().update(b, ops);

                logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                        new Object[]{a.getLogin(), b.getLogin()});

                future.complete(fs);
            });


        }, resultHandler);

    }

    public void getFriendshipByFriends(User a, User b, Handler<AsyncResult<Friendship>> resultHandler){

        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Looking for friendship between {0} and {1}",
                    new Object[]{a.getLogin(), b.getLogin()});

            Friendship friendshipA =  this.createQuery().filter("userAsker", a).filter("userAsked", b).get();
            Friendship friendshipB =  this.createQuery().filter("userAsker", b).filter("userAsked", a).get();

            Friendship friendship = friendshipA == null ? friendshipB : friendshipA;

            logger.log(Level.INFO, "{0} and {1}'s friendship {3} found",
                    new Object[]{a.getLogin(), b.getLogin(), friendship == null ? "not" : ""}); //mdr ?

            future.complete(friendship);
        }, resultHandler);

    }


    public UserDTO toDTO(Friendship friendship, User me){
        return friendship.getUserAsker().equals(me)
                ? new UserDTO(friendship.getUserAsked())
                : new UserDTO(friendship.getUserAsker());
    }

    public List<UserDTO> toDTO(List<Friendship> friendships, User me){
        return friendships.stream().map(x ->
                    x.getUserAsker().equals(me)
                    ? new UserDTO(x.getUserAsked())
                    : new UserDTO(x.getUserAsker()))
                .collect(Collectors.toList());
    }

    public FriendshipDTO toFriendshipDTO(Friendship friendship, User me){
        return friendship.getUserAsker().equals(me)
                ? new FriendshipDTO(friendship, friendship.getUserAsked())
                : new FriendshipDTO(friendship, friendship.getUserAsker());
    }

    public List<FriendshipDTO> toFriendshipDTO(List<Friendship> friendships, User me){
        return friendships.stream().map(x ->
                x.getUserAsker().equals(me)
                        ? new FriendshipDTO(x,x.getUserAsked())
                        : new FriendshipDTO(x,x.getUserAsker()))
                .collect(Collectors.toList());
    }
}
