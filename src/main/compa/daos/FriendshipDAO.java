package compa.daos;

import compa.dtos.FriendshipDTO;
import compa.models.Friendship;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.app.DAO;
import compa.models.User;
import org.bson.types.ObjectId;
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
        Object[] params = new Object[]{user.getUsername(), status.name()};

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for {0}'s friends {1}", params);
            Query<Friendship> query = this.createQuery();
            query.or(
                    query.and(
                            query.criteria("userA").equal(user),
                            query.criteria("statusA").equal(status)
                    ),

                    query.and(
                            query.criteria("userB").equal(user),
                            query.criteria("statusB").equal(status)
                    )
            );

            List<User> friends = query.asList().stream()
                    .map((fs) -> fs.getUserA().equals(user) ? fs.getUserB() : fs.getUserA())
                    .collect(Collectors.toList());

            logger.log(Level.INFO, "Found {0} friends", friends.size());
            future.complete(friends);

        }, resultHandler);
    }

    public void findFriendshipByUsers(User a, User b, Handler<AsyncResult<Friendship>> resultHandler){
        Object[] params = new Object[]{a.getUsername(), b.getUsername()};

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Looking for friendship between {0} and {1}", params);
            Query<Friendship> query = this.createQuery();

            query.or(
                    query.and(
                            query.criteria("userA").equal(a),
                            query.criteria("userB").equal(b)
                    ),
                    query.and(
                            query.criteria("userA").equal(b),
                            query.criteria("userB").equal(a)
                    )
            );

            Friendship friendship = this.findOne(query);

            if(friendship == null)
                logger.log(Level.INFO, "Did not find friendship between {0} and {1}", params);
            else
                logger.log(Level.INFO, "Found friendship between {0} and {1}", params);

            future.complete(friendship);

        }, resultHandler);
    }

    public void addFriendship(User me, User friend, Handler<AsyncResult<Friendship>> resultHandler) {
        Object[] params = new Object[]{me.getUsername(), friend.getUsername()};

        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Adding a friendship between {0} and {1}", params);
            Friendship fs = new Friendship(me, friend);
            this.save(fs);
            logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}", params);
            future.complete(fs);

        }, resultHandler);
    }

    public void updateFriendship(Friendship f, Friendship.Status m, boolean isA, Handler<AsyncResult<Friendship>> resultHandler){
        System.out.println("...........................................................................;");
        Object[] params = isA
                ? new Object[]{f.getUserA().getUsername(), f.getUserB().getUsername(), m.toString()}
                : new Object[]{f.getUserB().getUsername(), f.getUserA().getUsername(), m.toString()};

        logger.log(Level.INFO, "{0} is updating his friendship with {1} to {2}", params);

        vertx.executeBlocking( future -> {
            if(isA)
                f.setStatusA(m);
            else
                f.setStatusB(m);

            this.save(f);

            logger.log(Level.INFO, "{0} has updated his friendship with {1} to {2}", params);
            future.complete(f);

        }, resultHandler);
    }

    public void deleteFriendship(Friendship fs, Handler<AsyncResult<Boolean>> resultHandler){
        Object[] params = new Object[]{fs.getUserB().getUsername(), fs.getUserA().getUsername()};
        logger.log(Level.INFO, "Deleting friendship between {0} and {1}", params);

        vertx.executeBlocking( future -> {
            this.delete(fs);
            logger.log(Level.INFO, "Deleted friendship between {0} and {1}", params);
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