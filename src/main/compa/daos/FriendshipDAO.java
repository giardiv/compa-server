package main.compa.daos;

import main.compa.app.DAO;
import main.compa.models.Friendship;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

public class FriendshipDAO extends DAO<FriendshipDAO, ObjectId> {

    public FriendshipDAO(Datastore ds){
        super(Friendship.class, ds);
    }
}
