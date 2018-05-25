package compa.daos;

import compa.app.Container;
import compa.dtos.LocationDTO;
import compa.exception.RegisterException;
import compa.models.Location;
import compa.app.DAO;
import compa.models.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import javax.swing.text.BadLocationException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LocationDAO extends DAO<Location, ObjectId> {

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
            Location lastLocation = me.getLastLocation();

            if(lastLocation.getLatitude()== lat && lastLocation.getLongitude() == lon) {
                future.complete(lastLocation);
            }else {
                Location location = new Location(lat, lon, date);
                this.save(location);
                me.addLocation(location);
                future.complete(location);

            }

            return;

        }, resultHandler);
    }

    public void getLocationFromDateInterval(User me, Date firstDate, Date lastDate, Handler<AsyncResult<List<Location>>> resultHandler){

    }
}
