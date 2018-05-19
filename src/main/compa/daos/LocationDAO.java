package main.compa.daos;

import main.compa.dtos.LocationDTO;
import main.compa.models.Location;
import main.compa.app.DAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.stream.Collectors;

public class LocationDAO extends DAO<Location, ObjectId> {

    public LocationDAO(Datastore ds){
        super(Location.class, ds);
    }

    public LocationDTO toDTO(Location location){
        return new LocationDTO(location);
    }

    public List<LocationDTO> toDTO(List<Location> locations){
        return locations.stream().map(x -> new LocationDTO(x)).collect(Collectors.toList());
    }
}
