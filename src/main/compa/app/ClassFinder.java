package compa.app;

import org.mongodb.morphia.utils.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ClassFinder {

    private static final String MODEL_DIRECTORY = "compa.models";
    private static final String DAO_DIRECTORY = "compa.daos";
    private static final String CONTROLLER_DIRECTORY = "compa.controllers";
    private static final String SERVICE_DIRECTORY = "compa.services";
    private static final String EXCEPTION_DIRECTORY = "compa.exception";

    public String getModelDirectory(){
        return MODEL_DIRECTORY;
    }

    public Map<Class, DAO> getDAOs(Container container){

        try {
            Set<Class<?>> classes = ReflectionUtils.getClasses(MODEL_DIRECTORY);
            classes = classes.stream().filter(x -> x.getEnclosingClass() == null).collect(Collectors.toSet());

            Map<Class, DAO> daos = new HashMap<>();

            for (Class clazz : classes) {

                try{
                    Class<?> daoClass = Class.forName(DAO_DIRECTORY + "." + clazz.getSimpleName() + "DAO");
                    daos.put(clazz, (DAO)  daoClass.getDeclaredConstructor(Container.class).newInstance(container));
                }
                catch(ClassNotFoundException e){
                    System.err.println("no dao for class " + clazz.toString() + ", used custom" );
                    //daos.put(clazz, new DAO<>(clazz, container));
                } catch(NoSuchMethodException | IllegalAccessException
                        | InstantiationException | InvocationTargetException e){
                    System.err.println(e.getMessage());
                }
            }

            for(Map.Entry<Class, DAO> dao : daos.entrySet()) {
                dao.getValue().init(daos);
            }

            return daos;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("problem getting classes from model directory");
            e.printStackTrace();
            return null;
        }

    }

    public List<Controller> getControllers(Container container){

        try {
            Set<Class<?>>  classes = ReflectionUtils.getClasses(CONTROLLER_DIRECTORY);
            classes = classes.stream().filter(x -> x.getEnclosingClass() == null).collect(Collectors.toSet());

            List<Controller> list = new ArrayList<>();

            for (Class<?> clazz : classes) {
                try {
                    list.add((Controller) clazz.getDeclaredConstructor(Container.class)
                            .newInstance(container));
                } catch(NoSuchMethodException | IllegalAccessException |
                        InstantiationException | InvocationTargetException e){
                    e.printStackTrace();
                }
            }

            return list;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("problem getting classes from controller directory");
            return null;
        }

    }

    public Map<Class, Service> getServices(Container container){

        /*
            UGLY FIX : reflection also returns anonymous inner classes...
            GsonService instanciates a JsonSerializer in itself and redefines a method
            considered as a class redefinition so it's returned as one of the classes of the
            service package. Therefore, we have to check whether the class is enclosed in another
        */

        try {
            Set<Class<?>>  classes = ReflectionUtils.getClasses(SERVICE_DIRECTORY);
            classes = classes.stream().filter(x -> x.getEnclosingClass() == null).collect(Collectors.toSet());

            Map<Class, Service> services = new HashMap<>();

            for (Class<?> clazz : classes) {

                try{
                    services.put(clazz, (Service)  clazz.getDeclaredConstructor(Container.class).newInstance(container));
                }
                catch(NoSuchMethodException | IllegalAccessException
                        | InstantiationException | InvocationTargetException e){
                    System.err.println(e.getMessage());
                }
            }

            return services;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("problem getting classes from service directory");
            return null;
        }

    }

    public Set<Class<?>> getExceptions(){
        try {
            return ReflectionUtils.getClasses(EXCEPTION_DIRECTORY);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
