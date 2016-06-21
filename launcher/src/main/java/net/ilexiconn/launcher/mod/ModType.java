package net.ilexiconn.launcher.mod;

import net.ilexiconn.launcher.Launcher;

import java.io.File;

public enum ModType {
    MOD(Launcher.INSTANCE.modsDir),
    COREMOD(Launcher.INSTANCE.coreModsDir),
    CONFIG(Launcher.INSTANCE.dataDir);

    private File file;

    ModType(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
