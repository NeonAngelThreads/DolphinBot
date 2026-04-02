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
 *    program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.managers;

import org.angellock.impl.Start;
import org.angellock.impl.managers.utils.AbstractJsonAccessor;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class ResourceHelper extends AbstractJsonAccessor {
    private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class.getCanonicalName());
    private final String fileType;
    public ResourceHelper(@Nullable String defaultPath, String fileType) {
        this.fileType = fileType;
        File outFile = new File((defaultPath != null) ? defaultPath : getBaseConfigRoot());
        if (!outFile.exists()){
            if (defaultPath != null) {
                log.warn(ConsoleTokens.colorizeText("&eSpecified config file &c" + defaultPath + "&e not found, &6reading from the default file: &3." + File.separator + getFullFileName()));
            } else {
                log.warn(ConsoleTokens.colorizeText("&eResource file path &5" + getFullFileName() + "&e is &dNOT-SET, &6reading from the default file: &3." + File.separator + getFullFileName()));
            }
            outFile = new File(this.getBaseConfigRoot());
        }
        if (outFile.isDirectory()) {
            outFile = new File(outFile, getFullFileName());
        }
        try {
            this.autoCopy(outFile);
        } catch (IOException e) {
            log.info(ConsoleTokens.colorizeText("&8" + e));
        }
        this.configPath = outFile.toPath();
    }

    private void autoCopy(File outFile) throws IOException {
        if (!outFile.exists()) {
            InputStream in = Start.class.getClassLoader().getResourceAsStream(getFullFileName());
            if (in != null) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                log.info(ConsoleTokens.colorizeText("&9Extract config file &3." + File.separator + getFullFileName()));

            }
            else {
                log.error(ConsoleTokens.colorizeText("&dCould not extract fallback config file &3." + File.separator + getFullFileName()));
            }
        }
//        this.configPath = Path.of(output, getFullFileName());
    }

    public String getFullFileName(){
        return this.getFileName() + this.fileType;
    }

}
