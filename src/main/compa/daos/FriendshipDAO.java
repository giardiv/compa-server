package main.compa.daos;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import main.compa.app.DAO;
import main.compa.app.DAO;
import main.compa.app.MongoUtil;
import main.compa.exception.FriendshipException;
import main.compa.models.Friendship;
import main.compa.models.User;
import main.compa.models.Friendship;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.*;
import sun.rmi.server.UnicastServerRef;

import javax.jws.soap.SOAPBinding;
import java.util.List;

public class FriendshipDAO extends DAO<Friendship, ObjectId> {

    public FriendshipDAO(Datastore ds){
        super(Friendship.class, ds);
    }

    public List<Friendship> getFriendshipByUserId(String id){
        Criteria criteriaLeft = this.createQuery().criteria("friendLeft").equal(id);
        Criteria criteriaRight = this.createQuery().criteria("friendRight").equal(id);
        Query<Friendship> query =  this.createQuery();
        query.or(new Criteria[]{criteriaLeft, criteriaRight});
        return this.find(query).asList();
    }

    public void addFriendship(User a, User b) throws FriendshipException {
        System.out.println("In addFriendship");
        Friendship fs = this.getFriendshipByFriends(a, b);
        String login_a = a.getLogin();

        if(fs != null) {
            throw new FriendshipException(FriendshipException.FRIEND_ALREADY_EXIST);
        }
        fs = new Friendship(a, b);
        this.save(fs);

        Query<User> query = MongoUtil.getDatastore().find(User.class).field("login").equal(login_a);
        UpdateOperations<User> update = MongoUtil.getDatastore().createUpdateOperations(User.class).set("friendships", fs);
        MongoUtil.getDatastore().update(query, update);
        System.out.println("Updated User friend");
    }

    public Friendship getFriendshipByFriends(User a, User b){
        Friendship friendshipA =  this.createQuery().filter("friendLeft", a).filter("friendRight", b).get();
        Friendship friendshipB =  this.createQuery().filter("friendLeft", b).filter("friendRight", a).get();
        if(friendshipA == null && friendshipB == null){
            return null;
        }
        if(friendshipA != null){
            return friendshipA;
        } else {
            return friendshipB;
        }
    }
}
