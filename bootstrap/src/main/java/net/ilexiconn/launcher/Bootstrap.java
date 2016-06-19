package net.ilexiconn.launcher;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.ilexiconn.launcher.version.Version;
import net.ilexiconn.launcher.version.VersionAdapter;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Bootstrap {
    public static final String URL = "http://pastebin.com/raw/kT55bi26";

    public File dataDir;
    public JsonParser jsonParser;
    public Gson gson;

    public Version currentVersion;
    public String currentMD5;

    public Version newerVersion;
    public String newerURL;
    public String actualMD5;

    public static void main(String[] args) {
        List<String> argumentList = Arrays.asList(args);
        Bootstrap bootstrap = new Bootstrap(argumentList.contains("--portable") || argumentList.contains("-p"));

        try {
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bootstrap(boolean portable) {
        this.dataDir = portable ? new File(".") : this.getDataFolder();
        this.jsonParser = new JsonParser();
        this.gson = new GsonBuilder().registerTypeAdapter(Version.class, new VersionAdapter()).create();

        File bootstrapDir = new File(this.dataDir, "bootstrap");
        if (bootstrapDir.exists()) {
            try {
                JsonObject object = this.jsonParser.parse(new FileReader(new File(bootstrapDir, "bootstrap.json"))).getAsJsonObject();
                this.currentVersion = new Version(object.get("version").getAsString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File launcherFile = new File(bootstrapDir, "launcher.jar");
            try {
                this.currentMD5 = Files.hash(launcherFile, Hashing.md5()).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        Map<Version, JsonObject> map = this.gson.fromJson(new InputStreamReader(new URL(Bootstrap.URL).openStream()), new TypeToken<Map<Version, JsonObject>>(){}.getType());
        for (Map.Entry<Version, JsonObject> entry : map.entrySet()) {
            int compare = entry.getKey().compareTo(this.currentVersion);
            if (compare > 0) {
                this.newerVersion = entry.getKey();
                this.newerURL = entry.getValue().get("url").getAsString();
            } else if (compare == 0) {
                this.actualMD5 = entry.getValue().get("md5").getAsString();
            }
        }

        if (this.newerVersion != null) {
            System.out.println("Found newer version " + this.newerVersion);
            System.out.println(this.newerURL);
        }
        if (this.currentMD5 != null && !this.currentMD5.equals(this.actualMD5)) {
            System.out.println("Invalid jar!");
        }
    }

    public File getDataFolder() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new File(System.getenv("APPDATA"), ".revival-launcher");
        } else if (osName.contains("mac")) {
            return new File(System.getProperty("user.home"), "/Library/Application Support/revival-launcher");
        } else {
            return new File(System.getProperty("user.home"), ".revival-launcher");
        }
    }
}
