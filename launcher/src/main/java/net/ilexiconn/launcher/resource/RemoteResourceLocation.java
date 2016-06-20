package net.ilexiconn.launcher.resource;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;

public class RemoteResourceLocation extends ResourceLocation {
    protected String urlString;
    protected URL url;

    public RemoteResourceLocation(String location, String url) {
        super(location);
        this.urlString = url;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.url == null) {
            this.url = new URL(this.urlString);
        }
        return this.url.openStream();
    }

    @Override
    public InputStream checkCache(File cacheDir) throws IOException {
        File assetsDir = new File(cacheDir, "assets" + File.separator + "launcher");
        if (assetsDir.exists()) {
            File resource = new File(assetsDir, this.getLocation());
            if (resource.exists()) {
                return new FileInputStream(resource);
            }
        }
        return null;
    }

    @Override
    public void cacheResource(InputStream stream, File cacheDir) throws IOException {
        File assetsDir = new File(cacheDir, "assets" + File.separator + "launcher");
        if (assetsDir.exists() || assetsDir.mkdirs()) {
            OutputStream output = new FileOutputStream(new File(assetsDir, this.getLocation()));
            IOUtils.copy(stream, output);
            output.close();
        }
    }
}
