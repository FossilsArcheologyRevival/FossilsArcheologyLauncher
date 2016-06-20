package net.ilexiconn.launcher.mod;

import com.google.gson.JsonObject;

public class ModConfig {
    private String file;
    private String url;

    public ModConfig(JsonObject object) {
        this.file = object.get("file").getAsString();
        this.url = object.get("url").getAsString();
    }

    public String getFile() {
        return file;
    }

    public String getURL() {
        return url;
    }
}
