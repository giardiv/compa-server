package compa;

import compa.app.ClassFinder;
import compa.app.Container;
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
public class TestTest {

    Vertx vertx;

    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess());
        c.run(new ClassFinder());
        vertx = c.getVertx();
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test1(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(8080, "localhost", "/", resp -> {
            resp.bodyHandler(body -> {
                context.assertEquals("test", "test");
                client.close();
                async.complete();
            });
        });
    }

}
