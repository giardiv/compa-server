package compa.app;

import com.google.gson.JsonElement;
import compa.dtos.UserDTO;

public abstract class Service {

    protected Container container;

    public Service(Container container){
        this.container = container;
    }

    protected Service get(Class service){
        return container.getServices().get(service);
    }
}
