package main.compa.app;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import main.compa.models.Location;

import java.io.IOException;

public class Adapter extends TypeAdapter<Location> {
    @Override
    public void write(JsonWriter jsonWriter, Location location) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name");
        jsonWriter.value(location.getLatitude() + 200000000.0);
        jsonWriter.name("rollNo");
        jsonWriter.value(location.getLongitude());
        jsonWriter.endObject();
    }

    public Location read(JsonReader reader) throws IOException {
        return new Location();
    }
}
