package compa.daos;

import compa.app.Container;
import compa.dtos.LocationDTO;
import compa.models.Location;
import compa.app.DAO;
import compa.models.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LocationDAO extends DAO<Location, ObjectId> {


    private Logger logger = Logger.getLogger("location_dao");
    private static final int PERIOD = -5;

    private UserDAO userDAO;

    public LocationDAO(Container container){
        super(Location.class, container);
    }

    public LocationDTO toDTO(Location location){
        return new LocationDTO(location);
    }

    public List<LocationDTO> toDTO(List<Location> locations){
        return locations.stream().map(LocationDTO::new).collect(Collectors.toList());
    }

    public void addPosition(User me, Double lat, Double lon, Date date, Handler<AsyncResult<Location>> resultHandler) {
        vertx.executeBlocking( future -> {

            logger.log(Level.INFO, "Creating a new location for {0}", me.getUsername());
            Location location = new Location(me.getId().toString(),lat, lon, date);
            this.save(location);
            me.addLocation(location);
            this.userDAO.save(me);
            logger.log(Level.INFO, "Created a new location for {0}", me.getUsername());
            future.complete(location);
        }, resultHandler);
    }

    public void getLocationFromDateInterval(User me, Handler<AsyncResult<List<Location>>> resultHandler){
        Date startDate, endDate;
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, PERIOD);
        endDate = calendar.getTime();
        System.out.println("data start : " + startDate);
        System.out.println("endDate  : " + endDate);

        vertx.executeBlocking( future -> {
            System.out.println("me : " + me.getLocations().size());

            List<Location> locationList = me.getLocations().size() > 0 ?
                    me.getLocations().stream()
                            .filter(d -> d.getDatetime().after(endDate) && d.getDatetime().before(startDate))
                            .collect(Collectors.toList()):
                    null;
            System.out.println("size : " + locationList.size());
            future.complete(locationList);
        }, resultHandler);
    }


    public void getLocationsFromUser(User me, Handler<AsyncResult<List<Location>>> resultHandler){
        vertx.executeBlocking( future -> {
            List<Location> locationList = me.getLocations();
            //TODO WHY WAS THIS THERE WHEN THE LOCATIONS WERENT LAZILY LOADED??
            future.complete(locationList);
        }, resultHandler);
    }

    @Override
    public void init(Map<Class, DAO> daos) {
        this.userDAO = (UserDAO) daos.get(User.class);
    }
}
