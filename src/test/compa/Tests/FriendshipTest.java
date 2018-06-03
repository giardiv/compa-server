package compa.Tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Controller;
import compa.app.Exception;
import compa.exception.FriendshipException;
import compa.exception.ParameterException;
import compa.exception.RegisterException;
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
    static List<User> users = new ArrayList<>();;
    private JsonObject testFriendship;

    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess(), Container.MODE.TEST);
        c.run(new ClassFinder());
        vertx = c.getVertx();
        this.gson = (GsonService) c.getServices().get(GsonService.class);
        datastore = c.getMongoUtil().getDatastore();
        dropData();
        fakeData();
    }

    @After
    public void after(TestContext context) {vertx.close(context.asyncAssertSuccess());}

    public static void fakeData(){
        int userNb = 5;

        for(String username : USER_LIST) {
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User( username + "@mail.fr", username, username, encPassword, salt);
            u.setToken(USER_TOKEN);
            users.add(u);
            datastore.save(u);
        }
        Random r = new Random();

        for(int i = 0; i < userNb - 1; ++i){
            User me = users.get(i);
            for(int j = i +2; j < userNb - 1; ++j){ // user 1 et 0 sont pas amis
                User friend = users.get(j);

                Friendship fs_me = new Friendship(me, friend);

                int n = j;
                switch(j){
                    case 0:
                        fs_me.setStatusA(Friendship.Status.BLOCKED);
                        break;
                    case 1:
                        fs_me.setStatusA(Friendship.Status.REFUSED);
                        break;
                    case 2:
                        fs_me.setStatusA(Friendship.Status.ACCEPTED);
                        break;
                    default:
                        break;
                }
                datastore.save(fs_me);
            }
        }
    }

    public static void dropData(){
        datastore.getCollection(Friendship.class).drop();
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
*/
}
