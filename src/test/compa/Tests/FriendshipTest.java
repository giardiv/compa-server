package compa.Tests;

import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Controller;
import compa.exception.FriendshipException;
import compa.exception.ParameterException;
import compa.exception.UserException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(VertxUnitRunner.class)
public class FriendshipTest {
    public static String USER_TOKEN = "q5ZV67c7MOBSNv97";
    public static int N_FAKE_USER = 10;
    public static String MAIL_POST = "@email.com";
    public static String[] USER_LIST = {
            "BASIC", //   1 ACCEPTED, 1 PENDING, 1 AWAITING, 1 BLOCKED, 1 BLOCKER
            "ALONE", //   0 ACCEPTED, 0 PENDING, 0 AWAITING, 0 BLOCKED, 0 BLOCKER

            "OTHER1", //  1 ACCEPTED, 0 PENDING, 0 AWAITING, 0 BLOCKED, 0 BLOCKER
            "OTHER2", //  0 ACCEPTED, 1 PENDING, 0 AWAITING, 0 BLOCKED, 0 BLOCKER
            "OTHER3", //  0 ACCEPTED, 0 PENDING, 1 AWAITING, 0 BLOCKED, 0 BLOCKER
            "OTHER4", //  0 ACCEPTED, 0 PENDING, 0 AWAITING, 1 BLOCKED, 0 BLOCKER
            "OTHER5" //   0 ACCEPTED, 0 PENDING, 0 AWAITING, 0 BLOCKED, 1 BLOCKER
    };

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
        int userNb = 10;
        List<User> users = new ArrayList<>();

        for(String username : USER_LIST) {
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User( username + "@mail.fr", username, username, encPassword, salt);
            u.setToken(USER_TOKEN);
            users.add(u);
            datastore.save(u);
        }
        Random r = new Random();


    }

    public static void dropData(){
        datastore.getCollection(Friendship.class).drop();
    }

    @Test
    public void addFriendshipWork(TestContext context) {
//        HttpClient client = vertx.createHttpClient();
//        Async async = context.async();
//
//        //JsonObject localUser = this.testRegisterUser.deepCopy();
//        localUser.remove("password");
//        localUser.addProperty("password", "tacos");
//
//        final String json = localUser.toString();
//        final String length = Integer.toString(json.length());


    }

    @Test
    public void addFriendshipBeFriend(TestContext context) {
    }

    @Test
    public void deleteFriendshipWork(TestContext context) {
    }

    @Test
    public void setFriendshipStatus(TestContext context) {
    }

    @Test
    public void getFriendshipByStatus(TestContext context) {
    }

    @Test
    public void searchFriendshipWork(TestContext context) {
    }

}
