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
    private ModType modType;

    private boolean hasConfig;
    private ModConfig[] configs;

    public Mod(String name, JsonObject object) {
        this.name = name;
        this.fileName = object.get("file").getAsString();
        this.url = object.get("url").getAsString();
        this.md5 = object.has("md5") ? null : object.get("md5").getAsString().toLowerCase(Locale.ENGLISH);
        this.modType = object.has("type") ? ModType.valueOf(object.get("type").getAsString().toUpperCase(Locale.ENGLISH)) : ModType.MOD;

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

    public ModType getModType() {
        return modType;
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
                return this.getMD5() != null && !this.getMD5().equals(md5);
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
