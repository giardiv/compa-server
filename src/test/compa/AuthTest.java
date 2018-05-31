package compa;

import com.google.gson.JsonObject;
import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Exception;
import compa.exception.LoginException;
import compa.exception.RegisterException;
import compa.models.Location;
import compa.models.User;
import compa.services.AuthenticationService;
import compa.services.GsonService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;

@RunWith(VertxUnitRunner.class)
public class AuthTest {

    Vertx vertx;
    static GsonService gson;
    static Datastore datastore;

    public static String USER_EMAIL = "shelby@gmail.com";
    public static String USER_LOGIN = "tom75";
    public static String USER_NAME = "Tommy";
    public static String USER_RAW_PW = "Burmingham_zer";
    public static String USER_TOKEN = "q5ZV67c7MOBSNv97";

    private JsonObject testRegisterUser;

    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess(), Container.MODE.TEST);
        c.run(new ClassFinder());
        vertx = c.getVertx();
        this.gson = (GsonService) c.getServices().get(GsonService.class);
        datastore = c.getMongoUtil().getDatastore();
        dropData();
        fakeData();

        testRegisterUser = new JsonObject();
        testRegisterUser.addProperty("email", "available_and_correct_email@yahoo.fr");
        testRegisterUser.addProperty("login", "available_logipn");
        testRegisterUser.addProperty("password", "correct_password");
        testRegisterUser.addProperty("name", "Bobby");
    }

    public static void dropData(){
        datastore.getCollection(User.class).drop();
    }
    public static void fakeData(){
        String salt = AuthenticationService.getSalt();
        String encPassword = AuthenticationService.encrypt(USER_RAW_PW, salt);
        User u = new User(USER_EMAIL, USER_NAME,USER_LOGIN, encPassword, salt.toString());
        u.setToken(USER_TOKEN);
        datastore.save(u);
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void registerPasswordTooShort(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject localUser = this.testRegisterUser.deepCopy();
        localUser.remove("password");
        localUser.addProperty("password", "tacos");
        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/register")
            .putHeader("content-type", "application/json")
            .putHeader("content-length", length)
            .handler(resp -> {
                context.assertEquals(resp.statusCode(), 400);
                resp.bodyHandler(body -> {
                    Exception e = gson.toObject(body.toString(), Exception.class);
                    context.assertEquals(e.getCode(), RegisterException.PASSWORD_TOO_SHORT.getKey());
                    client.close();
                    async.complete();
                });
            })
            .end(json);
    }

    @Test
    public void registerLoginExist(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        localUser.remove("login");
        localUser.addProperty("login", USER_LOGIN);

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/register")
            .putHeader("content-type", "application/json")
            .putHeader("content-length", length)
            .handler( resp -> {
                context.assertEquals(resp.statusCode(), 400);
                resp.bodyHandler(body -> {
                    Exception e = gson.toObject(body.toString(), Exception.class);
                    context.assertEquals(e.getCode(), RegisterException.USER_ALREADY_EXIST.getKey());
                    client.close();
                    async.complete();
                });
            })
            .end(json);
    }

    // TODO: test e-mail

    @Test
    public void registerWork(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject localUser = this.testRegisterUser.deepCopy();

        final String json = localUser.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/register")
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
            .end(json);
    }

    @Test
    public void loginWork(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        credentials.addProperty("login", USER_LOGIN);
        credentials.addProperty("password", USER_RAW_PW);

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
            .end(json);
    }


    @Test
    public void loginBadPassword(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        JsonObject credentials = new JsonObject();
        credentials.addProperty("login", USER_LOGIN);
        credentials.addProperty("password", USER_RAW_PW + ".wrong");

        final String json = credentials.toString();
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/login")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), LoginException.INCORRECT_CREDENTIALS.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .end(json);
    }

    @Test
    public void badToken(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        client.request(HttpMethod.GET, Container.SERVER_PORT, Container.SERVER_HOST, "/user",
                resp -> {
                    context.assertEquals(resp.statusCode(), 401);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), LoginException.INCORRECT_TOKEN.getKey());
                        client.close();
                        async.complete();
                    });
                }
        ).putHeader("authorization", "wrong_token").end();
    }

    @Test
    public void goodToken(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        client.request(HttpMethod.GET, Container.SERVER_PORT, Container.SERVER_HOST, "/user",
                resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    resp.bodyHandler(body -> {
                        JsonObject e = gson.toObject(body.toString(), JsonObject.class);
                        context.assertEquals(e.get("name").getAsString(),USER_NAME);
                        client.close();
                        async.complete();
                    });
                }
        ).putHeader("authorization", USER_TOKEN).end();
    }
}
