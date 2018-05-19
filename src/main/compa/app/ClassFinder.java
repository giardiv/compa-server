package main.compa.app;

import io.vertx.ext.web.Router;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ClassFinder {

    private static final String MODEL_DIRECTORY = "main.compa.models";
    private static final String DAO_DIRECTORY = "main.compa.daos";
    private static final String CONTROLLER_DIRECTORY = "main.compa.controllers";
    private static final String SERVICES_DIRECTORY = "main.compa.services";

    public String getModelDirectory(){
        return MODEL_DIRECTORY;
    }

    public Map<Class, BasicDAO> getDAOs(Datastore ds){

        try {
            Set<Class<?>> classes = ReflectionUtils.getClasses(MODEL_DIRECTORY);
            classes = classes.stream().filter(x -> x.getEnclosingClass() == null).collect(Collectors.toSet());

            Map<Class, BasicDAO> daos = new HashMap<>();

            for (Class clazz : classes) {

                try{
                    Class<?> daoClass = Class.forName(DAO_DIRECTORY + "." + clazz.getSimpleName() + "DAO");
                    daos.put(clazz, (DAO)  daoClass.getDeclaredConstructor(Datastore.class).newInstance(ds));
                }
                catch(ClassNotFoundException e){
                    System.err.println("no dao for class " + clazz.toString() + ", used custom" );
                    daos.put(clazz, new DAO<>(clazz, ds));
                } catch(NoSuchMethodException | IllegalAccessException
                        | InstantiationException | InvocationTargetException e){
                    System.err.println(e.getMessage());
                }
            }

            return daos;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("problem getting classes from model directory");
            e.printStackTrace();
            return null;
        }

    }

    public List<Controller> getControllers(ServiceManager serviceManager, Router router, ModelManager modelManager){

        try {
            Set<Class<?>>  classes = ReflectionUtils.getClasses(CONTROLLER_DIRECTORY);
            classes = classes.stream().filter(x -> x.getEnclosingClass() == null).collect(Collectors.toSet());

            List<Controller> list = new ArrayList<>();

            for (Class<?> clazz : classes) {
                try {
                    list.add((Controller) clazz.getDeclaredConstructor(ServiceManager.class, Router.class, ModelManager.class)
                            .newInstance(serviceManager, router, modelManager));
                } catch(NoSuchMethodException | IllegalAccessException |
                        InstantiationException | InvocationTargetException e){
                    System.err.println(e.getMessage());
                }
            }

            return list;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("problem getting classes from controller directory");
            return null;
        }

    }

    public Set<Class<?>> getServices(){

        /*
            UGLY FIX : reflection also returns anonymous inner classes...
            GsonService instanciates a JsonSerializer in itself and redefines a method
            considered as a class redefinition so it's returned as one of the classes of the
            service package. Therefore, we have to check whether the class is enclosed in another
        */

        try {
            Set<Class<?>>  classes = ReflectionUtils.getClasses(SERVICES_DIRECTORY);
            return classes.stream().filter(x -> x.getEnclosingClass() == null).collect(Collectors.toSet());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("problem getting classes from service directory");
            return null;
        }

    }
}
