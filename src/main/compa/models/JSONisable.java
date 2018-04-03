package main.compa.models;

import com.google.gson.GsonBuilder;

public interface JSONisable {

    default String toJSON(){
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }
}
