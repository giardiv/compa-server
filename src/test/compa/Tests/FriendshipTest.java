package compa.Tests;

import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.models.Friendship;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;

@RunWith(VertxUnitRunner.class)
public class FriendshipTest {

    Vertx vertx;
    static GsonService gson;
    static Datastore datastore;


    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess(), Container.MODE.TEST);
        c.run(new ClassFinder());
        vertx = c.getVertx();
        this.gson = (GsonService) c.getServices().get(GsonService.class);
        datastore = c.getMongoUtil().getDatastore();
        dropData();
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    public static void fakeData(){
        int userNb = 50;

        for(int i = 0; i < userNb; ++i) {
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User("email" + i + "@mail.fr", "Name " + i, "user" + i, encPassword, salt);
        }
    }

    public static void dropData(){
        datastore.getCollection(Friendship.class).drop();
    }


}
