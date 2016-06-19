package net.ilexiconn.launcher.version;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class VersionAdapter extends TypeAdapter<Version>  {
    @Override
    public void write(JsonWriter out, Version value) throws IOException {
        out.value(value.get());
    }

    @Override
    public Version read(JsonReader in) throws IOException {
        return new Version(in.nextString());
    }
}
