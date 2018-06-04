package compa.Tests;

import com.fasterxml.jackson.databind.JsonNode;
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
    };

    Vertx vertx;
    static GsonService gson;
    static Datastore datastore;
    static List<User> users = new ArrayList<>();;
    private JsonObject testFriendship;

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

    public static void fakeData(){

        Map<String , User> users = new HashMap<>();

        for(TestUser username : TestUser.values()) {
            String un = username.toString();
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt(PASSWORD, salt);
            User u = new User( un + MAIL_POST, un, un, encPassword, salt);
            u.setToken(USER_TOKEN);
            users.put(un, u);
        }
      
        datastore.save(users);

        List<Friendship> fs = new ArrayList<>();

        for (TestUser username: TestUser.values()
             ) {
            if(username != TestUser.BASIC && username != TestUser.ALONE && !username.toString().contains("OTHER")){
                Friendship f = new Friendship(users.get(TestUser.BASIC), users.get(username));
                f.setStatusB(Friendship.Status.valueOf(username.toString().toUpperCase()));
                fs.add(f);
            }
        }

        datastore.save(fs);
    }

    public static void dropData(){
        datastore.getCollection(Friendship.class).drop();
        datastore.getCollection(User.class).drop();
    }

    @Test
    public void addFriendshipExist(TestContext context){
        //usre
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject localUser = this.testFriendship.deepCopy();


        localUser.remove("friend_id");
        localUser.addProperty("friend_id", users.get(2).getId().toString());

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), FriendshipException.FRIENDSHIP_ALREADY_EXISTS.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .end(json);
    }
    /*
    @Test
    public void addFriendshipWork(TestContext context) {

        /**HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();

        final String json = credentials.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/login")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(body -> {
                        JsonObject e = gson.toObject(body.toString(), JsonObject.class);
                        context.assertNotNull(e.get("token"));
                        client.close();
                        async.complete();
                    });
                })
                .end(json);**/
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
*/
}
