package net.ilexiconn.launcher;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import uk.co.rx14.jmclaunchlib.LaunchSpec;
import uk.co.rx14.jmclaunchlib.LaunchTask;
import uk.co.rx14.jmclaunchlib.LaunchTaskBuilder;
import uk.co.rx14.jmclaunchlib.util.OS;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Launcher {
    public File dataDir;
    public File configFile;
    public JsonObject config;

    public static void main(String[] args) {
        List<String> argumentList = Arrays.asList(args);
        Launcher launcher = new Launcher(argumentList.contains("--portable") || argumentList.contains("-p"));

        try {
            launcher.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Launcher(boolean portable) {
        this.dataDir = portable ? new File(".") : this.getDataFolder();
        this.configFile = new File(this.dataDir, "launcher.json");
        if (this.configFile.exists()) {
            try {
                this.config = new JsonParser().parse(new FileReader(this.configFile)).getAsJsonObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            this.config = new JsonObject();
            this.writeDefaultConfig(this.config);
            try {
                if (!this.configFile.createNewFile()) {
                    throw new RuntimeException("Failed to create the config file");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create the config file");
            }
            try {
                FileUtils.writeStringToFile(this.configFile, new GsonBuilder().setPrettyPrinting().create().toJson(this.config));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeDefaultConfig(JsonObject config) {
        config.addProperty("javaHome", System.getProperty("java.home"));
        config.addProperty("keepLauncherOpen", false);
        config.addProperty("jvmArguments", "");
    }

    public void start() throws IOException {
        LaunchTask task = new LaunchTaskBuilder()
                .setCachesDir(new File(this.dataDir, "cache").toPath())
                .setForgeVersion("1.7.10", "1.7.10-10.13.4.1558-1.7.10")
                .setInstanceDir(this.dataDir.toPath())

                .setUsername("iLexiconn")
                .setOffline()

                /*.setUsername("")
                .setPasswordSupplier(new PasswordSupplier() {
                    @Override
                    public String getPassword(String username, boolean retry, String failureMessage) {
                        return "";
                    }
                })*/

                .build();

        LaunchSpec launchSpec = task.getSpec();
        Process process = launchSpec.run(Paths.get(this.config.get("javaHome").getAsString(), "bin", OS.getCURRENT() == OS.WINDOWS ? "java.exe" : "java"));

        InputStream inputStream = process.getInputStream();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
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
