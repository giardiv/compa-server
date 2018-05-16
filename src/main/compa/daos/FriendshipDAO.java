package main.compa.daos;

import main.compa.app.DAO;
import main.compa.exception.FriendshipException;
import main.compa.models.Friendship;
import main.compa.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;

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
        Friendship fs = this.getFriendshipByFriends(a, b);
        if(fs != null)
            throw new FriendshipException(FriendshipException.FRIEND_ALREADY_EXIST);
        fs = new Friendship(a, b);
        this.save(fs);
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
