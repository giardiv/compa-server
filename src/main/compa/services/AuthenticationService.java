package main.compa.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import main.compa.app.Container;
import main.compa.app.Service;
import main.compa.daos.UserDAO;
import main.compa.models.User;

public class AuthenticationService extends Service {

    public AuthenticationService(Container container){
        super(container);
    }

    public void checkAuth(HttpServerRequest request, Handler<AsyncResult<User>> resultHandler){

        String token = request.getHeader("token");

        if(token == null) {
            Future<User> f = Future.failedFuture("no token in header");
            f.setHandler(resultHandler);;
        }
        else{

            ((UserDAO) container.getDAO(User.class)).findOne("token", token, res -> {
                User u = res.result();
                Future<User> f = (res.result() == null)
                        ? Future.failedFuture("no user found with this token")
                        : Future.succeededFuture(u);
                f.setHandler(resultHandler);
            });
        };

    }

}
