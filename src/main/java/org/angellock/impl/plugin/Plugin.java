/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *    License for more details. You should have received a copy of the GNU General Public License along with this
 *    program. If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.plugin;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;

import java.nio.file.Path;
import java.util.List;

public interface Plugin {
    Path getDataFolder();
    String getName();
    String getVersion();
    String getDescription();
    boolean isEnabled();
    void setPluginManifest(Manifest name);
    void setEnabled(boolean state);
    List<SessionListener> getListeners();
    void onDisable();

    void onLoad();

    void onPreEnable(final RobotPlayer entityBot);
}
