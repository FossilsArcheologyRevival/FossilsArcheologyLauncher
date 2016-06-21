package net.ilexiconn.launcher.resource.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ilexiconn.launcher.resource.ResourceLoader;
import net.ilexiconn.launcher.resource.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Translator {
    public static final String DEFAULT_LANGUAGE = "en_US";

    private String currentLanguage = Translator.DEFAULT_LANGUAGE;
    private Map<String, String> translationMap = new HashMap<>();

    public Translator(String language, ResourceLoader resourceLoader) {
        this.loadLanguage(language, resourceLoader);
    }

    public void loadLanguage(String language, ResourceLoader resourceLoader) {
        this.currentLanguage = language;
        InputStream stream = resourceLoader.loadStream(new ResourceLocation("lang/" + this.currentLanguage + ".json"));
        if (stream == null) {
            stream = resourceLoader.loadStream(new ResourceLocation("lang/" + Translator.DEFAULT_LANGUAGE + ".json"));
        }
        JsonObject object = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            this.translationMap.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    public String translate(String key, Object... args) {
        if (!this.translationMap.containsKey(key)) {
            return key;
        }
        return String.format(this.translationMap.get(key), args);
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
