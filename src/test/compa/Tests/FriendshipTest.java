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

        for(int i = 0; i < userNb; ++i) {
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User("email" + i + "@mail.fr", "Name " + i, "user" + i, encPassword, salt);
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
