package main.compa.app;

public abstract class Service {

    protected Container container;

    public Service(Container container){
        this.container = container;
    }
}
