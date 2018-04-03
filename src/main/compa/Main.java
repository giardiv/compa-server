package main.compa;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import main.compa.app.Container;
import main.compa.app.Controller;
import main.compa.app.ModelManager;
import main.compa.app.ControllerFactory;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class Main extends AbstractVerticle{
	
	public static void main(String... args) {

		/*ControllerFactory cf = (Router router, ModelManager modelManager) -> {
			List<controllers> list = new ArrayList<>();
			list.add(new LocationController(router, modelManager));
			list.add( new UserController(router, modelManager));
			return list;
		};*/

		ControllerFactory cf2 = (Router router, ModelManager modelManager) -> {
            List<Controller> list = new ArrayList<>();

            try {
                for (Class clazz : ReflectionUtils.getClasses("main.compa.controllers")) {
                    Object c = clazz.getDeclaredConstructor(Router.class, ModelManager.class)
                            .newInstance(router, modelManager);
                    list.add((Controller) c );
                }

                return list;

            } catch (Exception e) {
                System.err.println("pb controller");
                return null;
            }
        };

		new Container().run(cf2);
	}

	private static void test(){
			
		Vertx vertx = Vertx.vertx();
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		server.requestHandler(router::accept).listen(8080);


		Route postLocation = router.route("/gps").handler(routingContext -> {

		});

		//NEXT
		Route route1 = router.route("/some/path/").handler(routingContext -> {
	
		  HttpServerResponse response = routingContext.response();
		  // enable chunked responses because we will be adding data as
		  // we execute over other handlers. This is only required once and
		  // only if several handlers do output.
		  response.setChunked(true);
	
		  response.write("route1\n");
	
		  // Call the next matching route after a 5 second delay
		  routingContext.vertx().setTimer(5000, tid -> routingContext.next());
		});
		
		Route route2 = router.route("/some/path/").handler(routingContext -> {
	
		  HttpServerResponse response = routingContext.response();
		  response.write("route2\n");
	
		  // Call the next matching route after a 5 second delay
		  routingContext.vertx().setTimer(5000, tid -> routingContext.next());
		});
		

		Route route3 = router.route("/some/path/").handler(routingContext -> {
			
		  HttpServerResponse response = routingContext.response();
		  response.write("route3");
	
		  // Now end the response
		  routingContext.response().end();
		});
		
			
		router.route("/blocking").blockingHandler(routingContext -> {
	
		  // Do something that might take some time synchronously
		  // Now call the next handler
		  routingContext.next();
	
		});
		
		//Blocking operations
		router.post("/some/endpoint/*").handler(ctx -> {
		  ctx.request().setExpectMultipart(true);
		  ctx.next();
		}).blockingHandler(ctx -> {
		  // ... Do some blocking operation
		});
		
		
		//Parameters
		Route route = router.route(HttpMethod.POST, "/catalogue/products/:producttype/:productid/");
	
		route.handler(routingContext -> {
	
		  String productType = routingContext.request().getParam("producttype");
		  String productID = routingContext.request().getParam("productid");
		});
		
		
		//By default routes are matched in the order they are added to the router.
		//When a request arrives the router will step through each route and check if it matches, if it matches then the handler for that route will be called.
		//If the handler subsequently calls next the handler for the next matching route (if any) will be called. And so on.
		//Routes are assigned an order at creation time corresponding to the order in which they were added to the router, with the first route numbered 0, the second route numbered 1, and so on.
		//By specifying an order for the route you can override the default ordering. Order can also be negative, e.g. if you want to ensure a route is evaluated before route number 0.
		// Change the order of route2 so it runs before route1
		//route.order(0);
		
		
		//Wildcards
		router.route().path("/some/path/*");
		router.route("/some/path/*");
		
		//Regex
		router.routeWithRegex(".*foo");
		router.route().pathRegex(".*foo");
		
		//Routing by Method Type
		router.get("/some/path/").handler(routingContext -> {});
		router.route().method(HttpMethod.POST).method(HttpMethod.PUT);
		router.route(HttpMethod.POST, "/some/path/"); 
		
		//Routing based on MIME type of request
		router.route().consumes("text/html").consumes("text/plain").handler(routingContext -> {
		  // This handler will be called for any request with
		  // content-type header set to `text/html` or `text/plain`.
		});
		
		router.route().consumes("text/*").handler(routingContext -> {
		  // This handler will be called for any request with top level type `text`
		  // e.g. content-type header set to `text/html` or `text/plain` will both match
		});
		
		router.route().consumes("*/json").handler(routingContext -> {
		  // This handler will be called for any request with sub-type json
		  // e.g. content-type header set to `text/json` or `application/json` will both match
		});

			
	}
}