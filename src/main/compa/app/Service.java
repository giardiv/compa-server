package compa.app;

public abstract class Service {

    protected Container container;

    public Service(Container container){
        this.container = container;
    }

    protected Service get(Class service){
        return container.getServices().get(service);
    }
}
