package main.compa.app;

import io.vertx.ext.web.Router;

import java.util.List;

public interface ControllerFactory {
    List<Controller> getControllers(Router router, ModelManager modelManager);
}
