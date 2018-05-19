package main.compa.daos;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import main.compa.app.DAO;
import main.compa.app.DAO;
import main.compa.app.MongoUtil;
import main.compa.dtos.LocationDTO;
import main.compa.dtos.UserDTO;
import main.compa.exception.FriendshipException;
import main.compa.models.Friendship;
import main.compa.models.Location;
import main.compa.models.User;
import main.compa.models.Friendship;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.*;
import sun.rmi.server.UnicastServerRef;

import javax.jws.soap.SOAPBinding;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FriendshipDAO extends DAO<Friendship, ObjectId> {

    Logger logger = Logger.getLogger("friendship_dao");

    public FriendshipDAO(Datastore ds){
        super(Friendship.class, ds);
    }

    public List<Friendship> getFriendshipsByUser(User user){

        /*vertx.executeBlocking({ future ->
                // Call some blocking API that takes a significant amount of time to return
                def result = someAPI.blockingMethod("hello")
                future.complete(result)
        }, { res ->
                println("The result is: ${res.result()}")
        })*/

        logger.log(Level.INFO, "Looking for {0}'s friends", user.getLogin());

        Query<Friendship> query = this.createQuery();
        query.or(
                query.criteria("friendLeft").equal(user),
                query.criteria("friendRight").equal(user)
        );
        List<Friendship> friendships = this.find(query).asList();
        logger.log(Level.INFO, "Found {0} friends", friendships.size());
        return friendships;
    }

    public Friendship addFriendship(User a, User b) throws FriendshipException {
        logger.log(Level.INFO, "Adding a friendship between {0} and {1}",
                new Object[]{a.getLogin(), b.getLogin()});

        Friendship fs = this.getFriendshipByFriends(a, b);

        if(fs != null) {
            logger.log(Level.WARNING, "Friendship between {0} and {1} already exists",
                    new Object[]{a.getLogin(), b.getLogin()});
            throw new FriendshipException(FriendshipException.FRIEND_ALREADY_EXIST);
        }

        fs = new Friendship(a, b);
        this.save(fs);

        UpdateOperations ops = getDatastore().createUpdateOperations(User.class).addToSet("friendships", fs);
        getDatastore().update(a, ops);
        getDatastore().update(b, ops);

        logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                new Object[]{a.getLogin(), b.getLogin()});

        return fs;

    }

    public Friendship getFriendshipByFriends(User a, User b){
        logger.log(Level.INFO, "Looking for friendship between {0} and {1}",
                new Object[]{a.getLogin(), b.getLogin()});

        Friendship friendshipA =  this.createQuery().filter("friendLeft", a).filter("friendRight", b).get();
        Friendship friendshipB =  this.createQuery().filter("friendLeft", b).filter("friendRight", a).get();

        Friendship friendship = friendshipA == null ? friendshipB : friendshipA;

        logger.log(Level.INFO, "{0} and {1}'s friendship {3} found",
                new Object[]{a.getLogin(), b.getLogin(), friendship == null ? "not" : ""}); //mdr

        return friendship;
    }

    public UserDTO toDTO(Friendship friendship, User me){
        return friendship.getFriendLeft().equals(me)
                ? new UserDTO(friendship.getFriendRight())
                : new UserDTO(friendship.getFriendLeft());
    }

    public List<UserDTO> toDTO(List<Friendship> friendships, User me){
        return friendships.stream().map(x ->
                    x.getFriendLeft().equals(me)
                    ? new UserDTO(x.getFriendRight())
                    : new UserDTO(x.getFriendLeft()))
                .collect(Collectors.toList());
    }
}
