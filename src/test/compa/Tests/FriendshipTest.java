package compa.Tests;

import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Controller;
import compa.app.Exception;
import compa.exception.FriendshipException;
import compa.exception.ParameterException;
import compa.exception.RegisterException;
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

    private JsonObject testRegisterUser;

    public static String FRIEND_ID = "tom75";//......

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
        int userNb = 30;
        List<User> users = new ArrayList<>();;

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
            for(int j = i; j < userNb - 1; ++j){
                User friend = users.get(j);

                Friendship fs_me = new Friendship(me, friend);

                int n = r.nextInt(1000);
                if(n % 5 == 0)
                    fs_me.setStatus(Friendship.Status.ACCEPTED, true);
                if(n % 7 == 0)
                    fs_me.setStatus(Friendship.Status.REFUSED, true);
                if(n % 11 == 0)
                    fs_me.setStatus(Friendship.Status.BLOCKED, true);
                datastore.save(fs_me);
                datastore.save(fs_me.getSister());
            }
        }

    }

    public static void dropData(){
        datastore.getCollection(Friendship.class).drop();
    }

    @Test
    public void addFriendshipWork(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        credentials.addProperty("friend_id", FRIEND_ID);

        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(body -> {
                        JsonObject e = gson.toObject(body.toString(), JsonObject.class);
                        context.assertNotNull(e.get("token")); //TODO renvoyer quoi
                        client.close();
                        async.complete();
                    });
                })
                .end(json);
    }

    @Test
    public void addFriendshipYourself(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        credentials.addProperty("friend_id", FRIEND_ID);

        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), FriendshipException.BEFRIEND_YOURSELF.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .end(json);
    }

    @Test
    public void addFriendshipAlreadyExists(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        credentials.addProperty("friend_id", FRIEND_ID);

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


    @Test
    public void deleteFriendshipWork(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        credentials.addProperty("friend_id", FRIEND_ID);

        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), FriendshipException.NOT_FRIEND.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .end(json);

    }

    @Test
    public void deleteFriendshipNotFriend(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        credentials.addProperty("friend_id", FRIEND_ID);

        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/friend")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), FriendshipException.NOT_FRIEND.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .end(json);
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
