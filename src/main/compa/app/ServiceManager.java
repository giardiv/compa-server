package main.compa.app;

import main.compa.services.GsonService;

import java.lang.Exception;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceManager {
    private Map<String, Service> services;

    public ServiceManager(Set<Class<?>> classes) {
        services = new HashMap<>();

        for(Class<?> clazz : classes){

            try {
                services.put(clazz.getSimpleName(),
                        (Service) clazz.getDeclaredConstructor().newInstance());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    public Service get(String name){
        return (Service) this.services.get(name);
    }
}
