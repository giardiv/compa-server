package compa.daos;

import compa.app.Container;
import compa.dtos.LocationDTO;
import compa.models.Location;
import compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

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
}
