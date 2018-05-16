package main.compa.daos;

import main.compa.app.DAO;
import main.compa.models.Friendship;
import main.compa.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class FriendshipDAO extends DAO<FriendshipDAO, ObjectId> {

    public FriendshipDAO(Datastore ds){
        super(Friendship.class, ds);
    }

    public List<FriendshipDAO> getFriendshipByUserId(String id){
        Criteria criteriaLeft = this.createQuery().criteria("userLeft").equal(id);
        Criteria criteriaRight = this.createQuery().criteria("userRight").equal(id);
        Query<FriendshipDAO> query =  this.createQuery();
        query.or(new Criteria[]{criteriaLeft, criteriaRight});
        return this.find(query).asList();
    }

    public void addFriendship(User a, User b){
        Friendship fs = this.createQuery().filter("login", login).get();
        //Friendship fs = new Friendship(a, b);
    }

    public Friendship getFriendshipByFriends(User a, User b){
        FriendshipDAO friendshipA =  this.createQuery().filter("userLeft", a).filter("userRight", b).get();
        //Friendship friendshipA = this.find(query).get().find();
    }
}
