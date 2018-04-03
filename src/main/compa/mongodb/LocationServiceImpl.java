package main.compa.mongodb;

import main.compa.Model.Location;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class LocationServiceImpl implements LocationService{

    private final Datastore datastoree;

    public LocationServiceImpl(Datastore datastoree) {
        this.datastoree = datastoree;
    }


    public Location save(double latitude, double longitude) {
        Location location = new Location(latitude,longitude);
        datastoree.save(location);
        return location;
    }

    @Override
    public Location find(String latitude, String longitude) {
        return null;
    }


    /*public Location find(double latitude, double longitude) {
        Query<Location> query = datastoree.find(Location.class);
        query.or(
                query.criteria("latitude").equal(latitude),
                query.criteria("longitude").equal(longitude)
        );
        return query;
    }*/

    @Override
    public List<Location> findAll() {
        return null;
    }

    @Override
    public void remove(String Latitude, String Longitude) {

    }
}
