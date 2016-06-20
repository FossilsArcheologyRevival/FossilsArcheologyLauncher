package net.ilexiconn.launcher;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.ilexiconn.launcher.mod.Mod;
import net.ilexiconn.launcher.mod.ModConfig;
import net.ilexiconn.launcher.ui.IProgressCallback;
import net.ilexiconn.launcher.ui.LauncherFrame;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import uk.co.rx14.jmclaunchlib.LaunchSpec;
import uk.co.rx14.jmclaunchlib.LaunchTask;
import uk.co.rx14.jmclaunchlib.LaunchTaskBuilder;
import uk.co.rx14.jmclaunchlib.auth.PasswordSupplier;
import uk.co.rx14.jmclaunchlib.auth.YggdrasilAuth;
import uk.co.rx14.jmclaunchlib.exceptions.ForbiddenOperationException;
import uk.co.rx14.jmclaunchlib.util.OS;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Launcher {
    public static final String URL = "http://pastebin.com/raw/EiE1kiP5";

    public File dataDir;
    public File configFile;
    public File cacheDir;
    public File modsDir;
    public File configDir;
    public JsonObject config;
    public boolean isCached;
    public JsonObject cache;

    public LauncherFrame frame;

    public static void main(String[] args) {
        List<String> argumentList = Arrays.asList(args);
        new Launcher(argumentList.contains("--portable") || argumentList.contains("-p"));
    }

    public Launcher(boolean portable) {
        this.dataDir = portable ? new File(".") : this.getDataFolder();
        this.configFile = new File(this.dataDir, "launcher.json");
        this.cacheDir = new File(this.dataDir, "cache");
        this.modsDir = new File(this.dataDir, "mods");
        this.configDir = new File(this.dataDir, "config");
        if (!this.dataDir.exists()) {
            if (!this.dataDir.mkdirs()) {
                throw new RuntimeException("Failed to create data dir");
            }
        }
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
            this.saveConfig();
        }
        if (this.cacheDir.exists()) {
            File authFile = new File(this.cacheDir, "auth.json");
            if (authFile.exists()) {
                try {
                    this.cache = new JsonParser().parse(new FileReader(authFile)).getAsJsonObject();
                    this.isCached = this.cache.get(this.config.get("username").getAsString()).getAsJsonObject().get("valid").getAsBoolean();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        this.frame = new LauncherFrame(this);
    }

    public void writeDefaultConfig(JsonObject config) {
        config.addProperty("username", "");
        config.addProperty("javaHome", System.getProperty("java.home"));
        config.addProperty("launcherBehaviour", 0);
        JsonArray array = new JsonArray();
        array.add("-Xmx1G");
        array.add("-XX:+UseConcMarkSweepGC");
        array.add("-XX:+CMSIncrementalMode");
        array.add("-XX:-UseAdaptiveSizePolicy");
        array.add("-Xmn128M");
        config.add("jvmArguments", array);
    }

    public void saveConfig() {
        try {
            FileUtils.writeStringToFile(this.configFile, new GsonBuilder().setPrettyPrinting().create().toJson(this.config), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMinecraft(PasswordSupplier passwordSupplier, final IProgressCallback progressCallback) throws IOException {
        try {
            if (!this.isCached) {
                YggdrasilAuth.auth(this.config.get("username").getAsString(), passwordSupplier.getPassword(null, false, null));
            }
        } catch (ForbiddenOperationException e) {
            this.frame.panel.play.setEnabled(true);
            this.frame.panel.username.setEnabled(true);
            this.frame.panel.password.setEnabled(true);
            return;
        }

        Map<String, JsonObject> map = new Gson().fromJson(new InputStreamReader(new URL(Launcher.URL).openStream()), new TypeToken<Map<String, JsonObject>>() {}.getType());
        List<Mod> modList = map.entrySet().stream().map(entry -> new Mod(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        if (!this.modsDir.exists()) {
            this.modsDir.mkdirs();
        }
        if (!this.configDir.exists()) {
            this.configDir.mkdirs();
        }
        File[] files = this.modsDir.listFiles();
        if (files != null) {
            List<String> modNames = modList.stream().map(Mod::getFileName).collect(Collectors.toList());
            for (File file : files) {
                if (!modNames.contains(file.getName())) {
                    System.out.println("Removing mod " + file.getName());
                    FileDeleteStrategy.FORCE.delete(file);
                }
            }
        }
        modList.removeIf(mod -> !mod.doDownload(new File(this.modsDir, mod.getFileName())));
        Exception e = this.downloadMods(modList);
        if (e != null) {
            this.frame.panel.play.setEnabled(true);
            this.frame.panel.username.setEnabled(true);
            this.frame.panel.password.setEnabled(true);
            return;
        }

        final LaunchTask task = new LaunchTaskBuilder()
                .setCachesDir(this.cacheDir.toPath())
                .setForgeVersion("1.7.10", "1.7.10-10.13.4.1558-1.7.10")
                .setInstanceDir(this.dataDir.toPath())
                .setUsername(this.config.get("username").getAsString())
                .setPasswordSupplier(passwordSupplier)
                .build();

        new Thread() {
            @Override
            public void run() {
                while (task.getCompletedPercentage() < 100) {
                    progressCallback.onProgress((int) task.getCompletedPercentage());
                }
                progressCallback.onProgress(100);
            }
        }.start();

        LaunchSpec launchSpec = task.getSpec();
        this.frame.panel.loadAvatar(launchSpec.getAuth().getSelectedProfile().getName());
        Process process = launchSpec.run(Paths.get(this.config.get("javaHome").getAsString(), "bin", OS.getCURRENT() == OS.WINDOWS ? "java.exe" : "java"));

        InputStream inputStream = process.getInputStream();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        if (this.config.get("launcherBehaviour").getAsInt() != 0) {
            this.frame.setVisible(true);
            this.frame.requestFocus();
        } else {
            this.frame.dispose();
        }
    }

    public Exception downloadMods(List<Mod> modList) {
        this.frame.panel.taskCount = modList.size();
        modList.stream().filter(Mod::hasConfig).forEach(mod -> Arrays.asList(mod.getConfigs()).forEach(config -> this.frame.panel.taskCount++));
        this.frame.panel.currentTask = -1;
        for (Mod mod : modList) {
            this.frame.panel.currentTask++;
            this.frame.panel.currentTaskName = "Downloading mod " + mod;
            Exception e = this.downloadFile(mod.getURL(), new File(this.modsDir, mod.getFileName()));
            if (e != null) {
                this.frame.panel.currentTaskName = e.getLocalizedMessage();
                return e;
            }
            if (mod.hasConfig()) {
                for (ModConfig config : mod.getConfigs()) {
                    this.frame.panel.currentTask++;
                    this.frame.panel.currentTaskName = "Downloading config " + config.getFile() + " for mod " + mod;
                    e = this.downloadFile(config.getURL(), new File(this.configDir, config.getFile()));
                    if (e != null) {
                        this.frame.panel.currentTaskName = e.getLocalizedMessage();
                        return e;
                    }
                }
            }
        }
        this.frame.panel.currentTaskName = "Launching Minecraft";
        this.frame.panel.currentTask++;
        return null;
    }

    public Exception downloadFile(String string, File file) {
        try {
            this.frame.panel.currentProgress = 0;
            URL url = new URL(string);
            HttpURLConnection connection = (HttpURLConnection) (url.openConnection());
            long contentLength = connection.getContentLength();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            OutputStream outputStream = new FileOutputStream(file);
            OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 1024);
            byte[] data = new byte[1024];
            long downloaded = 0;
            int i;
            while ((i = inputStream.read(data, 0, 1024)) >= 0) {
                downloaded += i;
                this.frame.panel.currentProgress = (int) ((((double) downloaded) / ((double) contentLength)) * 100.0D);
                bufferedOutputStream.write(data, 0, i);
            }
            bufferedOutputStream.close();
            inputStream.close();
            this.frame.panel.currentProgress = 0;
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return e;
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
