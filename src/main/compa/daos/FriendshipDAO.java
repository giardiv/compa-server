package compa.daos;

import compa.dtos.FriendshipDTO;
import compa.models.Friendship;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.app.DAO;
import compa.dtos.UserDTO;
import compa.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.*;

import java.util.ArrayList;
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
            logger.log(Level.INFO, "Looking for {0}'s friends", user.getUsername());
            Query<Friendship> query = this.createQuery();
            System.out.println(user.getId());
            //query.or(
                    query.and(
                            query.criteria("friendA").equal(user),
                            query.criteria("statusA").equal(status)
                    );
                    //,
                    //query.and(
                    //       query.criteria("friendBId").equal(user.getId().toString()),
                    //        query.criteria("statusB").equal(status))
            //);
            System.out.println(query.asList().size());
            List<User> friends = query.asList()
                    .stream()
                    .map((fs) -> fs.getFriendA() == user ? fs.getFriendB() : fs.getFriendA())
                    .collect(Collectors.toList()); //100% with you on this how dare you**/

            logger.log(Level.INFO, "Found {0} friends", friends.size());

            future.complete(friends);

        }, resultHandler);
    }

    public void findFriendshipByUser(User me, User friend, Handler<AsyncResult<Friendship>> resultHandler){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for friendship");
            Query<Friendship> query = this.createQuery();
            query.or(
                    query.and(
                            query.criteria("friendAId").equal(me),
                            query.criteria("friendBId").equal(friend)),
                    query.and(
                            query.criteria("friendAId").equal(friend),
                            query.criteria("friendBId").equal(me))

            );
            Friendship friendship = this.findOne(query);
            logger.log(Level.INFO, "Found {0} friends", friendship);
            future.complete(friendship);
        }, resultHandler);
    }

    /*public void findFriendshipOfUsers(User me, String friend, Handler<AsyncResult<List<Friendship>>> resultHandler){
        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for {0}'s friends", me.getUsername());
            Query<Friendship> query = this.createQuery();
            query.or(
                    query.criteria("friend").equal(me)
            );
            query.project("sister",true);
            List<Friendship> friendships = query.asList();

            future.complete(friendships);

        }, resultHandler);

    }*/

    public void addFriendship(User me, User friend, Handler<AsyncResult<Friendship>> resultHandler) {
     vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",new Object[]{me.getUsername(), friend.getUsername()});
            //Friendship fs = new Friendship(friend, me);
            //this.save(fs);
            /**this.save(fs.getSister());
            logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                    new Object[]{me.getUsername(), friend.getUsername()});
            future.complete(fs);**/
        }, resultHandler);
    }

    public void updateFriendship(Friendship f, Friendship.Status m, boolean isA, Handler<AsyncResult<Friendship>> resultHandler){
        Object[] params = new Object[]{f.getFriendA().getUsername(), f.getFriendB().getUsername()};
        logger.log(Level.INFO, "Updating friendship between {0} and {1}", params);

        vertx.executeBlocking( future -> {
            if(isA)
                f.setStatusA(m);
            else
                f.setStatusB(m);
            this.save(f);
            future.complete(f);
        }, resultHandler);
    }

    public void deleteFriendship(Friendship fs, Handler<AsyncResult<Boolean>> resultHandler){
        //logger.log(Level.INFO, "Deleting friendship between {0} and {1}",
                //new Object[]{fs.getFriend().getUsername(), fs.getSister().getFriend().getUsername()});

        vertx.executeBlocking( future -> {
            //this.delete(fs.getSister());
            this.delete(fs);
            future.complete();
        }, resultHandler);
    }

    public List<FriendshipDTO> toDTO(List<Friendship> friendships){
        return friendships.stream().map(FriendshipDTO::new).collect(Collectors.toList());
    }

    @Override
    public void init(Map<Class, DAO> daos) {

    }
}