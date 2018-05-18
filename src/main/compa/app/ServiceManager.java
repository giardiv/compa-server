package main.compa.app;

import main.compa.services.GsonService;

import java.lang.Exception;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceManager {
    private Map services;

    public ServiceManager(Set<Class<?>> classes) {
        services = new HashMap<String, Service>();

        Service g = new GsonService();
        for(Class<?> clazz : classes){
            System.out.println(clazz.getSimpleName());
            try{
                services.put(clazz.getSimpleName(), (Service) clazz.getDeclaredConstructor().newInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
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
