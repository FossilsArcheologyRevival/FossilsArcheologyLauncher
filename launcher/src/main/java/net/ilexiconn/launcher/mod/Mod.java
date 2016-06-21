package net.ilexiconn.launcher.mod;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class Mod {
    private String name;
    private String fileName;
    private String url;
    private String md5;
    private boolean versionSpecific;

    private boolean hasConfig;
    private ModConfig[] configs;

    public Mod(String name, JsonObject object) {
        this.name = name;
        this.fileName = object.get("file").getAsString();
        this.url = object.get("url").getAsString();
        this.md5 = object.get("md5").getAsString().toLowerCase(Locale.ENGLISH);
        this.versionSpecific = object.has("versionSpecific") && object.get("versionSpecific").getAsBoolean();

        this.hasConfig = object.has("config");
        if (this.hasConfig) {
            JsonArray array = object.get("config").getAsJsonArray();
            this.configs = new ModConfig[array.size()];
            for (int i = 0; i < array.size(); i++) {
                this.configs[i] = new ModConfig(array.get(i).getAsJsonObject());
            }
        }
    }

    public String getName() {
        return name;
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

    public boolean isVersionSpecific() {
        return versionSpecific;
    }

    public boolean hasConfig() {
        return hasConfig;
    }

    public ModConfig[] getConfigs() {
        return configs;
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
        return this.getName();
    }
}
