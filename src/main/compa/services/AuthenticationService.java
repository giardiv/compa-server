package main.compa.services;

import io.vertx.core.http.HttpServerRequest;
import main.compa.app.Container;
import main.compa.app.Service;
import main.compa.models.User;

public class AuthenticationService extends Service {

    public AuthenticationService(Container container){
        super(container);
    }

    public User checkAuth(HttpServerRequest request){
        String token = request.getHeader("token");
        if(token == null)
            return null;
        return container.getMongoUtil().getDatastore().createQuery(User.class).filter("token", token).get();
    }
    
}
