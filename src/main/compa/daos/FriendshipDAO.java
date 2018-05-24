package compa.daos;

import compa.models.Friendship;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import compa.app.Container;
import compa.app.DAO;
import compa.models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FriendshipDAO extends DAO<Friendship, ObjectId> {

    private Logger logger = Logger.getLogger("friendship_dao2");

    public FriendshipDAO(Container container){
        super(Friendship.class, container);
    }


    public void addFriendship(User a, User b, Handler<AsyncResult<Friendship>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",new Object[]{a.getLogin(), b.getLogin()});

                Friendship fs = new Friendship(a, b);
                this.save(fs);

                UpdateOperations<User> ops = getDatastore().createUpdateOperations(User.class).addToSet("friendships", fs);
                getDatastore().update(a, ops);
                getDatastore().update(b, ops);

                logger.log(Level.INFO, "Successfully added a friendship between {0} and {1}",
                        new Object[]{a.getLogin(), b.getLogin()});

                future.complete(fs);

        }, resultHandler);

    }



}
