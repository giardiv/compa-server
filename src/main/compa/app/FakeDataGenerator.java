package compa.app;

import compa.models.Friendship;
import compa.models.Location;
import compa.models.User;
import compa.services.AuthenticationService;
import org.mongodb.morphia.Datastore;

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

        List<User> users = new ArrayList<>();

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
            User u = new User("email" + i + "@toto.fr", "Name "+ i, "user" + i, encPassword, salt);
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
            for(int j = i+1; j < userNb - 1; ++j){
                User friend = users.get(j);

                Friendship fs = new Friendship(me, friend);

                int n = r.nextInt(4);
                switch(n){
                    case 0:
                        fs.setStatusA(Friendship.Status.BLOCKED);
                        break;
                    case 1:
                        fs.setStatusA(Friendship.Status.REFUSED);
                        break;
                    case 2:
                        fs.setStatusA(Friendship.Status.ACCEPTED);
                        break;
                    default:
                        break; //will be pending
                }

                datastore.save(fs);
            }
        }
        System.out.println("over");
    }

}
