package compa.Tests;

import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.models.Friendship;
import compa.models.Image;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTest {

    public static String USER_TOKEN = "q5ZV67c7MOBSNv97";


    Vertx vertx;

    static GsonService gson;
    static Datastore datastore;
    static List<User> users = new ArrayList<>();

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

    public void fakeData() {
        int userNb = 5;
        String username = "Name";
        for(int i = 0; i < userNb; ++i){
            String un = username;
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User("email" + i + "@toto.fr", username + i, "user" + i, encPassword, salt);
            u.setToken(getUserToken(un));
            users.add(u);
            datastore.save(u);
        }

        Friendship fs = new Friendship(users.get(0),users.get(1));
        fs.setStatusB(Friendship.Status.ACCEPTED);
        fs.setStatusA(Friendship.Status.ACCEPTED);
        datastore.save(fs);

    }

    public static void dropData(){
        datastore.getCollection(User.class).drop();
        datastore.getCollection(Friendship.class).drop();

    }

    private String getUserToken(String un){
        return USER_TOKEN + "." + un;
    }


    @Test
    public void getProfileWork(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        JsonObject body = new JsonObject();
        body.addProperty("friend_id", (users.get(1).getId().toString()));

        final String json = body.toString();
        final String length = Integer.toString(json.length());
        client.get(Container.SERVER_PORT, Container.SERVER_HOST, "/user/" + this.users.get(1).getId().toString())
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(response -> {
                        client.close();
                        async.complete();
                    });
                })
                .putHeader("Authorization", getUserToken(users.get(0).getToken()))
                .end(json);
    }
//
//    @Test
//    public void updateProfile(TestContext context) {
//    }
//
//    @Test
//    public void setGhostMode(TestContext context) {
//    }
}
