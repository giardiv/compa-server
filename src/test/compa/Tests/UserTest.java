package compa.Tests;

import compa.app.ClassFinder;
import compa.app.Container;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    Vertx vertx;

    @Before
    public void before(TestContext context) {
        Container c = new Container(context.asyncAssertSuccess(), Container.MODE.TEST);
        c.run(new ClassFinder());
        vertx = c.getVertx();
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void getUser(TestContext context) {
    }

    @Test
    public void getProfile(TestContext context) {
    }

    @Test
    public void updateProfile(TestContext context) {
    }

    @Test
    public void setGhostMode(TestContext context) {
    }
}
