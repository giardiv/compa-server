package compa.app;

import compa.models.Friendship;
import compa.models.Location;
import compa.models.User;
import compa.services.AuthenticationService;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeDataGenerator {

    public static void main(String... args){
        ClassFinder cf = new ClassFinder();
        Container c = new Container(null, Container.MODE.PROD);
        c.run(cf);

        List<User> users = new ArrayList<>();;

        Datastore datastore = c.getMongoUtil().getDatastore();

        datastore.getCollection(User.class).drop();
        datastore.getCollection(Location.class).drop();
        datastore.getCollection(Friendship.class).drop();

        Location cityTest = new Location(51.509865, -0.118092);

        double baseLatitude = Math.round((cityTest.getLatitude() - 0.007)*1000)/1000;
        double baseLongitude = Math.round((cityTest.getLongitude() - 0.008)*1000)/1000;

        int offset = -10000;
        int userNb = 50;
        int locationsPerUser = 5;

        for(int i = 0; i < userNb; ++i){
            String salt = AuthenticationService.getSalt();
            String encPassword = AuthenticationService.encrypt("password" + i, salt);
            User u = new User("email@mail.fr", "Name "+ i, "user" + i, encPassword, salt);
            for(int j = 0; j < locationsPerUser; ++j){

                LocalDateTime date = LocalDateTime.now().minus(offset, ChronoUnit.SECONDS);

                Location l = new Location(
                        baseLatitude + new Random().nextInt(140) * 0.0001,
                        baseLongitude + new Random().nextInt(140) * 0.0001,
                        java.sql.Timestamp.valueOf(date));

                datastore.save(l);
                u.addLocation(l);

                offset += 100;
            }

            users.add(u);
            datastore.save(u);
        }

       Random r = new Random();

        for(int i = 0; i < userNb - 1; ++i){
            User me = users.get(i);
            for(int j = i; j < userNb - 1; ++j){
                User friend = users.get(j);

                Friendship fs_me = new Friendship(me, friend);

                int n = r.nextInt(1000);
                if(n % 11 == 0)
                    fs_me.setStatus(Friendship.Status.BLOCKED, true);
                if(n % 7 == 0)
                    fs_me.setStatus(Friendship.Status.REFUSED, true);
                if(n % 2 == 0)
                    fs_me.setStatus(Friendship.Status.ACCEPTED, true);

                datastore.save(fs_me);
                datastore.save(fs_me.getSister());
            }
        }
    }

}
