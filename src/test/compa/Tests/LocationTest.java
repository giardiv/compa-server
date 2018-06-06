package compa.Tests;

import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Controller;
import compa.app.Exception;
import compa.models.Friendship;
import compa.models.Location;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RunWith(VertxUnitRunner.class)
public class LocationTest {
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
    static Map users = new HashMap<LocationTest.TestUser, User>();


    static Location cityTest = new Location(51.509865, -0.118092);

    static  double baseLatitude = Math.round((cityTest.getLatitude() - 0.007)*1000)/1000;
    static  double baseLongitude = Math.round((cityTest.getLongitude() - 0.008)*1000)/1000;

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
        Map<LocationTest.TestUser, User> users = new HashMap<LocationTest.TestUser, User>();

        int locationsPerUser = 5;
        int offset = -10000;

        for(LocationTest.TestUser username : LocationTest.TestUser.values()) {
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

        for (LocationTest.TestUser username: LocationTest.TestUser.values()) {
            if(username != LocationTest.TestUser.BASIC && username != LocationTest.TestUser.ALONE && !username.toString().contains("OTHER")){
                Friendship f = new Friendship(users.get(LocationTest.TestUser.BASIC), users.get(username));
                f.setStatusB(Friendship.Status.valueOf(username.toString().toUpperCase()));
                fs.add(f);
            }
        }

        Friendship f = new Friendship(users.get(LocationTest.TestUser.OTHER3), users.get(LocationTest.TestUser.OTHER2));
        f.setStatusB(Friendship.Status.ACCEPTED);
        fs.add(f);

        datastore.save(fs);


        for (LocationTest.TestUser username: LocationTest.TestUser.values()) {
            if(username != LocationTest.TestUser.BASIC && username != LocationTest.TestUser.ALONE && !username.toString().contains("OTHER")) {

                User u = users.get(username);
                for (int j = 0; j < locationsPerUser; ++j) {
                    LocalDateTime date = LocalDateTime.now().minus(offset, ChronoUnit.SECONDS);
                    Location l = new Location(
                            u.getId().toString(),
                            baseLatitude + new Random().nextInt(140) * 0.0001,
                            baseLongitude + new Random().nextInt(140) * 0.0001,
                            java.sql.Timestamp.valueOf(date));

                    datastore.save(l);
                    u.addLocation(l);

                    offset += 100;
                }
                datastore.save(u);
            }
        }

    }
    private String getUserToken(String un){
        return USER_TOKEN + "." + un;
    }


    public static void dropData(){
        datastore.getCollection(Location.class).drop();
        datastore.getCollection(Friendship.class).drop();
        datastore.getCollection(User.class).drop();
    }


    // based on fake location
    @Test
    public void getLocationWork(TestContext context) {

        HttpClient client = vertx.createHttpClient();

        Async async = context.async();
        LocalDateTime date = LocalDateTime.now().minus(-10000, ChronoUnit.SECONDS);

        JsonObject body = new JsonObject();
        body.addProperty("latitude", (baseLatitude + new Random().nextInt(140) * 0.0001));
        body.addProperty("longitude", (baseLongitude + new Random().nextInt(140) * 0.0001));
        body.addProperty("datetime", (java.sql.Timestamp.valueOf(date)).toString());

        final String json = body.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/location")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(response -> {
                        client.close();
                        async.complete();
                    });
                })
                .putHeader("Authorization", getUserToken(TestUser.OTHER1.toString()))
                .end(json);
    }

    @Test
    public void getLocationOnPeriod(TestContext context) {

    }

}
