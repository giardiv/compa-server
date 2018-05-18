package main.compa.app;

import io.vertx.ext.web.Router;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ClassFinder {

    private static final String MODEL_DIRECTORY = "main.compa.models";
    private static final String DAO_DIRECTORY = "main.compa.daos";
    private static final String CONTROLLER_DIRECTORY = "main.compa.controllers";

    public String getModelDirectory(){
        return MODEL_DIRECTORY;
    }

    public Map<Class, BasicDAO> getDAOs(Datastore ds){

        Set<Class<?>> classes = null;

        try {
            classes = ReflectionUtils.getClasses(MODEL_DIRECTORY);
        } catch (ClassNotFoundException e) {
            System.err.println("problem getting classes from model directory");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Class, BasicDAO> daos = new HashMap<>();

        for (Class clazz : classes) {

            try{
                Class<?> daoClass = Class.forName(DAO_DIRECTORY + "." + clazz.getSimpleName() + "DAO");
                daos.put(clazz, (DAO)  daoClass.getDeclaredConstructor(Datastore.class).newInstance(ds));
            }
            catch(ClassNotFoundException e){
                System.err.println("no dao for class " + clazz.toString() + ", used custom" );
                daos.put(clazz, new DAO<>(clazz, ds));
            } catch(NoSuchMethodException e){} catch(IllegalAccessException e){}
                catch(InstantiationException e){} catch(InvocationTargetException e){}
        }

        return daos;

    }

    public List<Controller> getControllers(Router router, ModelManager modelManager){

        Set<Class<?>> classes = null;

        try {
            classes = ReflectionUtils.getClasses(CONTROLLER_DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("problem getting classes from controller directory");
            return null;
        }

        List<Controller> list = new ArrayList<>();

        for (Class<?> clazz : classes) {

            try {
                list.add((Controller) clazz.getDeclaredConstructor(Router.class, ModelManager.class)
                        .newInstance(router, modelManager));
            } catch(NoSuchMethodException e){} catch(IllegalAccessException e){}
            catch(InstantiationException e){} catch(InvocationTargetException e){}

        }

        return list;

    }
}
