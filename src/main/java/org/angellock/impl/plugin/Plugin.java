package org.angellock.impl.plugin;

import org.angellock.impl.AbstractRobot;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;

import java.nio.file.Path;
import java.util.List;

public interface Plugin {
    Path getDataFolder();
    String getName();
    String getVersion();
    String getDescription();
    boolean isEnabled();
    void setManifest(Manifest name);
    void setEnabled(boolean state);
    List<SessionListener> getListeners();
    void onDisable();

    void onLoad();

    void onEnables(final AbstractRobot entityBot);
}
