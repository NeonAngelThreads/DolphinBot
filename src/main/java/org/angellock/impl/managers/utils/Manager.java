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

package org.angellock.impl.managers.utils;

import org.angellock.impl.Start;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Manager {
    private static String PATH = null;
    public String getBaseConfigRoot(){
        if (PATH == null) {
            URL d = Start.class.getProtectionDomain().getCodeSource().getLocation();
            String path = URLDecoder.decode(d.getPath(), StandardCharsets.UTF_8);
            if (Start.isWindows() && path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith(".jar")) {
                path = path.substring(0, path.lastIndexOf('/'));
            }
            if (!path.endsWith("/")) {
                path += '/';
            }
            PATH = path;
            return PATH;
        }
        return PATH;
    }
}
