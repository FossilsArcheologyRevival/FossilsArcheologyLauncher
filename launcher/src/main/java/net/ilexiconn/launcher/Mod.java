package net.ilexiconn.launcher;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

public class Mod {
    private String fileName;
    private String url;
    private String md5;

    private boolean hasConfig;
    private String configFile;
    private String configURL;

    public Mod(String fileName, JsonObject object) {
        this.fileName = fileName;
        this.url = object.get("url").getAsString();
        this.md5 = object.get("url").getAsString();

        this.hasConfig = object.has("config");
        if (this.hasConfig) {
            object = object.get("config").getAsJsonObject();
            this.configFile = object.get("file").getAsString();
            this.configURL = object.get("url").getAsString();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getURL() {
        return url;
    }

    public String getMD5() {
        return md5;
    }

    public boolean hasConfig() {
        return hasConfig;
    }

    public String getConfigFile() {
        return configFile;
    }

    public String getConfigURL() {
        return configURL;
    }

    public boolean doDownload(File file) {
        if (!file.exists()) {
            return true;
        } else {
            try {
                String md5 = Files.hash(file, Hashing.md5()).toString();
                return !this.getMD5().equals(md5);
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
    }

    @Override
    public String toString() {
        return this.getFileName() + "[url: " + this.getURL() + ", md5: " + this.getMD5() + ", config: " + this.hasConfig() + "]";
    }
}
