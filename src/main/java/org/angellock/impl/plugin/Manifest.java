package org.angellock.impl.plugin;

public class Manifest {
    private String entry;
    private String name;
    private String version;

    public String getMainClass() {
        return entry;
    }

    public String getPluginName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Manifest{" +
                "entry='" + entry + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
