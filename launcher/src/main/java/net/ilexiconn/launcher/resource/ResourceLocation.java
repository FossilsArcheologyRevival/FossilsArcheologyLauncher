package net.ilexiconn.launcher.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLocation {
    protected String location;

    public ResourceLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return "/assets/" + this.location;
    }

    public InputStream getInputStream() throws IOException {
        return ResourceLocation.class.getResourceAsStream(this.getLocation());
    }

    public InputStream checkCache(File cacheDir) throws IOException {
        return null;
    }

    public void cacheResource(InputStream stream, File cacheDir) throws IOException {

    }
}
