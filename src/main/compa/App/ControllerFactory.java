package main.compa.App;

import io.vertx.ext.web.Router;
import main.compa.App.Controller;
import main.compa.App.ModelManager;

import java.util.List;

public interface ControllerFactory {
    public List<Controller> getControllers(Router router, ModelManager modelManager);
}
