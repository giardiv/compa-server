package compa.daos;

import compa.app.Container;
import compa.dtos.LocationDTO;
import compa.models.Location;
import compa.app.DAO;
import compa.models.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationDAO extends DAO<Location, ObjectId> {

    private UserDAO userDAO;

    public LocationDAO(Container container){
        super(Location.class, container);
    }

    public LocationDTO toDTO(Location location){
        return new LocationDTO(location);
    }

    public List<LocationDTO> toDTO(List<Location> locations){
        return locations.stream().map(x -> new LocationDTO(x)).collect(Collectors.toList());
    }

    public void addPosition(User me, Double lat, Double lon, Date date, Handler<AsyncResult<Location>> resultHandler) {
        vertx.executeBlocking( future -> {

            Location location = new Location(lat, lon, date);
            this.save(location);
            me.addLocation(location);
            this.userDAO.save(me);
            future.complete(location);

            return;

        }, resultHandler);
    }

    public void getLocationFromDateInterval(User me, Date startDate, Date endDate, Handler<AsyncResult<List<Location>>> resultHandler){
        vertx.executeBlocking( future -> {

            List<Location> locationList = me.getLocations().size() > 0 ?
                    me.getLocations().stream()
                            .filter(d -> d.getDatetime().after(startDate) && d.getDatetime().before(endDate))
                            .collect(Collectors.toList()):
                    null;
            future.complete(locationList);
        }, resultHandler);
    }


    public void getLocationFromUser(User me, Handler<AsyncResult<List<Location>>> resultHandler){
        vertx.executeBlocking( future -> {
            List<Location> locationList = me.getLocations();
            future.complete(locationList);
        }, resultHandler);
    }

    @Override
    public void init(Map<Class, DAO> daos) {
        this.userDAO = (UserDAO) daos.get(User.class);
    }
}
