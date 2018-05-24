package compa.daos;

import compa.dtos.FriendshipDTO;
import compa.models.Friendship2;
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

public class Friendship2DAO extends DAO<Friendship2, ObjectId> {

    private Logger logger = Logger.getLogger("friendship_dao2");

    public Friendship2DAO(Container container){
        super(Friendship2.class, container);
    }


    public void addFriendship(User a, User b, Handler<AsyncResult<Friendship2>> resultHandler) {

        vertx.executeBlocking( future -> {
            logger.log(Level.INFO, "Adding a friendship between {0} and {1}",new Object[]{a.getLogin(), b.getLogin()});

                Friendship2 fs = new Friendship2(a, b);
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