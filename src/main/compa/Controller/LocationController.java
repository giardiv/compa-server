package main.compa.Controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonArray;
import main.compa.Model.Location;
import main.compa.App.Controller;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class LocationController extends Controller {

    private static final String PREFIX = "/location";

    public ArrayList<Location> testData(){
    	ArrayList<Location> locations = new ArrayList<Location>();
    	 /*locations.add(new Location(1.1, 2.1));
         locations.add(new Location(1.2, 2.2));
         locations.add(new Location(1.3, 2.3));*/
         return locations;         
    }
    
    public LocationController(){
        super(PREFIX);
        this.registerRoute(HttpMethod.POST, "/", this::newInstance);
        this.registerRoute(HttpMethod.GET, "/", this::getAll);
        this.registerRoute(HttpMethod.GET, "/:id", this::get);

    }

    private void newInstance(RoutingContext routingContext){

    }

    
    private void get(RoutingContext routingContext){
  	  String id = routingContext.request().getParam("id");
 	 routingContext.response()
	 	.putHeader("content-type", "application/json; charset=utf-8")
	 	.end(Json.encodePrettily(testData()));
	}

    private void getAll(RoutingContext routingContext){
    	 routingContext.response()
    	 	.putHeader("content-type", "application/json; charset=utf-8")
    	 	.end(Json.encodePrettily(testData()));
    }
    
   
}
