package net.ilexiconn.launcher.version;

public class Version implements Comparable<Version> {
    private String version;

    public String get() {
        return this.version;
    }

    public Version(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        } else if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
            throw new IllegalArgumentException("Invalid version format");
        }
        this.version = version;
    }

    @Override
    public int compareTo(Version version) {
        if (version == null) {
            return 1;
        }
        String[] thisParts = this.get().split("\\.");
        String[] otherParts = version.get().split("\\.");
        int length = Math.max(thisParts.length, otherParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < otherParts.length ? Integer.parseInt(otherParts[i]) : 0;
            if (thisPart < thatPart) {
                return -1;
            } else if (thisPart > thatPart) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && this.getClass() == obj.getClass() && this.compareTo((Version) obj) == 0;
    }

    @Override
    public String toString() {
        return this.get();
    }
}
