package compa.Tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Exception;
import compa.exception.FriendshipException;
import compa.exception.LoginException;
import compa.exception.RegisterException;
import compa.models.Friendship;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;

import java.util.*;

@RunWith(VertxUnitRunner.class)
public class FriendshipTest {
    public static String MAIL_POST = "@email.com";
    public static String PASSWORD = "password";
    public static String USER_TOKEN = "q5ZV67c7MOBSNv97";

    public static enum TestUser {
            BASIC,    //  1 ACCEPTED, 1 PENDING, 1 AWAITING, 1 BLOCKED, 1 BLOCKER

            ACCEPTED, //  1 ACCEPTED, 0 PENDING, 0 AWAITING, 0 BLOCKED, 0 BLOCKER
            PENDING,  //  0 ACCEPTED, 1 PENDING, 0 AWAITING, 0 BLOCKED, 0 BLOCKER
            AWAITING, //  0 ACCEPTED, 0 PENDING, 1 AWAITING, 0 BLOCKED, 0 BLOCKER
            BLOCKED,  //  0 ACCEPTED, 0 PENDING, 0 AWAITING, 1 BLOCKED, 0 BLOCKER
            BLOCKER,  //  0 ACCEPTED, 0 PENDING, 0 AWAITING, 0 BLOCKED, 1 BLOCKER

            ALONE,     //  0 ACCEPTED, 0 PENDING, 0 AWAITING, 0 BLOCKED, 0 BLOCKER

            OTHER1,
            OTHER2,
            OTHER3,
    }

    Vertx vertx;
    static GsonService gson;
    static Datastore datastore;
    static Map users = new HashMap<TestUser, User>();

    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess(), Container.MODE.TEST);
        c.run(new ClassFinder());
        vertx = c.getVertx();
        gson = (GsonService) c.getServices().get(GsonService.class);
        datastore = c.getMongoUtil().getDatastore();
        dropData();
        fakeData();
    }

    @After
    public void after(TestContext context) {vertx.close(context.asyncAssertSuccess());}

    public void fakeData(){

        Map<TestUser , User> users = new HashMap<TestUser, User>();

        for(TestUser username : TestUser.values()) {
            String un = username.toString();
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt(PASSWORD, salt);
            User u = new User( un + MAIL_POST, un, un, encPassword, salt);

            u.setToken(getUserToken(un));
            users.put(username, u);
        }

        this.users = users;
        datastore.save(users.values());

        List<Friendship> fs = new ArrayList<>();

        for (TestUser username: TestUser.values()
             ) {
            if(username != TestUser.BASIC && username != TestUser.ALONE && !username.toString().contains("OTHER")){
                Friendship f = new Friendship(users.get(TestUser.BASIC), users.get(username));
                f.setStatusB(Friendship.Status.valueOf(username.toString().toUpperCase()));
                fs.add(f);
            }
        }

        // For delete test
        Friendship f = new Friendship(users.get(TestUser.OTHER3), users.get(TestUser.OTHER2));
        f.setStatusB(Friendship.Status.ACCEPTED);
        fs.add(f);

        datastore.save(fs);
    }

    private String getUserToken(String un){
        return USER_TOKEN + "." + un;
    }

    public static void dropData(){
        datastore.getCollection(Friendship.class).drop();
        datastore.getCollection(User.class).drop();
    }

    @Test
    public void addFriendshipWork(TestContext context){
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        JsonObject body = new JsonObject();
        body.addProperty("friend_id", ((User) this.users.get(TestUser.OTHER1)).getId().toString());

        final String json = body.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(response -> {
                        client.close();
                        async.complete();
                    });
                })
                .putHeader("Authorization", getUserToken(TestUser.OTHER2.toString()))
                .end(json);
    }


    @Test
    public void addFriendshipExist(TestContext context) {

        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        JsonObject body = new JsonObject();
        body.addProperty("friend_id", ((User) this.users.get(TestUser.ACCEPTED)).getId().toString());

        final String json = body.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(response -> {
                        Exception e = gson.toObject(response.toString(), Exception.class);
                        context.assertEquals(e.getCode(), FriendshipException.FRIENDSHIP_ALREADY_EXISTS.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .putHeader("Authorization", getUserToken(TestUser.BASIC.toString()))
                .end(json);
    }

    @Test
    public void deleteFriendshipWork(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        JsonObject body = new JsonObject();
        body.addProperty("friend_id", ((User) this.users.get(TestUser.OTHER2)).getId().toString());

        final String json = body.toString();
        final String length = Integer.toString(json.length());

        client.delete(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(response -> {
                        client.close();
                        async.complete();
                    });
                })
                .putHeader("Authorization", getUserToken(TestUser.OTHER3.toString()))
                .end(json);
    }

/**


    @Test
    public void setFriendshipStatus(TestContext context) {
    }

    @Test
    public void getFriendshipByStatus(TestContext context) {
    }

    @Test
    public void searchFriendshipWork(TestContext context) {
    }**/
}
