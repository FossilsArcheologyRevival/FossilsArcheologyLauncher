package net.ilexiconn.launcher.resource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    private File cacheDir;
    private Map<String, BufferedImage> imageMap = new HashMap<>();
    private Map<String, ImageIcon> iconMap = new HashMap<>();

    public ResourceLoader(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    public BufferedImage loadImage(ResourceLocation location) {
        if (!this.imageMap.containsKey(location.getLocation())) {
            try {
                InputStream stream = location.checkCache(this.cacheDir);
                if (stream == null) {
                    stream = location.getInputStream();
                    location.cacheResource(stream, this.cacheDir);
                }
                BufferedImage image = ImageIO.read(stream);
                stream.close();
                this.imageMap.put(location.getLocation(), image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.imageMap.get(location.getLocation());
    }

    public ImageIcon loadIcon(ResourceLocation location) {
        if (!this.imageMap.containsKey(location.getLocation())) {
            ImageIcon icon = new ImageIcon(ResourceLoader.class.getResource(location.getLocation()));
            this.iconMap.put(location.getLocation(), icon);
        }
        return this.iconMap.get(location.getLocation());
    }

    public InputStream loadStream(ResourceLocation location) {
        try {
            return location.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
