package net.ilexiconn.launcher;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.ilexiconn.launcher.version.Version;
import net.ilexiconn.launcher.version.VersionAdapter;
import org.apache.commons.io.FileUtils;

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

    public File bootstrapFile;
    public File launcherFile;

    public Version newerVersion;
    public String newerURL;

    public Progressbar progressbar;

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
        this.gson = new GsonBuilder().registerTypeAdapter(Version.class, new VersionAdapter()).setPrettyPrinting().create();

        File bootstrapDir = new File(this.dataDir, "bootstrap");
        this.bootstrapFile = new File(bootstrapDir, "bootstrap.json");
        this.launcherFile = new File(bootstrapDir, "launcher.jar");

        if (bootstrapDir.exists()) {
            if (this.bootstrapFile.exists()) {
                try {
                    JsonObject object = this.jsonParser.parse(new FileReader(this.bootstrapFile)).getAsJsonObject();
                    this.currentVersion = new Version(object.get("version").getAsString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (this.launcherFile.exists()) {
                try {
                    this.currentMD5 = Files.hash(this.launcherFile, Hashing.md5()).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!bootstrapDir.mkdirs()) {
                throw new RuntimeException("Unable to create data directory");
            }
        }

        this.progressbar = new Progressbar();
    }

    public void start() throws IOException {
        Map<Version, JsonObject> map = this.gson.fromJson(new InputStreamReader(new URL(Bootstrap.URL).openStream()), new TypeToken<Map<Version, JsonObject>>() {
        }.getType());
        for (Map.Entry<Version, JsonObject> entry : map.entrySet()) {
            int compare = entry.getKey().compareTo(this.currentVersion);
            if (compare > 0) {
                if (this.newerVersion != null) {
                    if (entry.getKey().compareTo(this.newerVersion) > 0) {
                        this.newerVersion = entry.getKey();
                        this.newerURL = entry.getValue().get("url").getAsString();
                    }
                } else {
                    this.newerVersion = entry.getKey();
                    this.newerURL = entry.getValue().get("url").getAsString();
                }
            } else if (compare == 0) {
                String actualMD5 = entry.getValue().get("md5").getAsString();
                if (!this.currentMD5.equals(actualMD5)) {
                    this.newerVersion = entry.getKey();
                    this.newerURL = entry.getValue().get("url").getAsString();
                }
            }
        }

        if (this.newerVersion != null) {
            System.out.println("Found newer version: " + this.newerVersion + " (currently " + this.currentVersion + ")");
            if (this.launcherFile.exists()) {
                System.out.println("Removing old jar");
                if (!this.launcherFile.delete()) {
                    throw new RuntimeException("Failed to remove old jar");
                }
            }
            this.progressbar.display(this.newerURL, this.launcherFile, new Progressbar.IProgressbarCallback() {
                @Override
                public void call() {
                    JsonObject object = new JsonObject();
                    object.addProperty("version", Bootstrap.this.newerVersion.get());
                    String json = Bootstrap.this.gson.toJson(object);
                    try {
                        FileUtils.writeStringToFile(Bootstrap.this.bootstrapFile, json, Charsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Update complete!");
                    Bootstrap.this.launch();
                }
            });
        } else {
            this.launch();
        }
    }

    public void launch() {
        String[] arguments = {"java", "-jar", this.launcherFile.getAbsolutePath()};
        arguments = this.concat(arguments, this.getLaunchArguments());
        ProcessBuilder process = new ProcessBuilder(arguments);
        process.directory(this.dataDir);
        try {
            process.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] concat(String[] a, String[] b) {
        int aLength = a.length;
        int bLength = b.length;
        String[] array = new String[aLength + bLength];
        System.arraycopy(a, 0, array, 0, aLength);
        System.arraycopy(b, 0, array, aLength, bLength);
        return array;
    }

    public String[] getLaunchArguments() {
        return new String[]{};
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
