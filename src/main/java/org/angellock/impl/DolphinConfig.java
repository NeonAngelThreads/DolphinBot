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

package org.angellock.impl;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

import java.util.Locale;
import java.util.Map;

@Data
@ToString
public class DolphinConfig {
    String server;
    int port;
    boolean autoReconnect;
    long packetFilterDelay;
    long msgSendDelay;
    int maxChunkView;
    long connectTimingOut;
    long reconnectDelay;
    @SerializedName("anti-AFK")
    boolean antiAFK;
    DebugSettings debugSettings;
    OtherSettings other;

    String language;

    @Data
    @ToString
    public static class DebugSettings {
        boolean enablePacketDebug;
        boolean showWarns;
    }

    @Data
    @ToString
    public static class OtherSettings {
        boolean enableSkinRecorder;
    }

    public DolphinConfig mergeCommandOptions(Map<String, Object> commandLines) {
        for (Map.Entry<String, Object> opt : commandLines.entrySet()) {
            String value = String.valueOf(opt.getValue());
            switch (opt.getKey().toLowerCase()) {
                case "server" -> setServer(value);
                case "port" -> setPort(Integer.parseInt(value));
                case "auto-reconnect", "reconnect" -> setAutoReconnect(true);
                case "max-chunk-view" -> setMaxChunkView(Integer.parseInt(value));
                case "debug" -> this.debugSettings.setEnablePacketDebug(true);
                case "enable-skin-recorder" -> this.other.setEnableSkinRecorder(true);
            }
        }
        return this;
    }

    public Locale getLanguage() {
        return getLanguage(this.language);
    }

    public static Locale getLanguage(String locale){
        return switch (locale) {
            case "zh" -> Locale.CHINESE;
            default -> Locale.ENGLISH;
        };
    }
}
