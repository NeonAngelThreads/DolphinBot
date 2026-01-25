package org.angellock.impl.plugin;

import org.geysermc.mcprotocollib.network.Session;

public interface SessionProvider {
    Session getSession();
}
