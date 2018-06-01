package compa.Tests;

import compa.app.ClassFinder;
import compa.app.Container;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FriendshipTest {
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
    public void addFriendshipWork(TestContext context) {
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



}
