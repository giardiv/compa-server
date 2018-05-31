package compa;

import compa.app.ClassFinder;
import compa.app.Container;
import compa.app.Exception;
import compa.exception.ParameterException;
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

@RunWith(VertxUnitRunner.class)
public class AppTest {

    Vertx vertx;
    GsonService gson;

    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess(), Container.MODE.TEST);
        c.run(new ClassFinder());
        vertx = c.getVertx();
        this.gson = (GsonService) c.getServices().get(GsonService.class);
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void missingParam(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        final String json = "{}";
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/register")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), ParameterException.PARAM_REQUIRED.getKey());
                        client.close();
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }


    // TODO test with token
    @Test
    public void wrongParam(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        final String json = "{}";
        final String length = Integer.toString(json.length());
        client.post(Container.SERVER_PORT, Container.SERVER_HOST, "/register")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler( resp -> {
                    context.assertEquals(resp.statusCode(), 400);
                    resp.bodyHandler(body -> {
                        Exception e = gson.toObject(body.toString(), Exception.class);
                        context.assertEquals(e.getCode(), 4001);
                        client.close();
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }
}
