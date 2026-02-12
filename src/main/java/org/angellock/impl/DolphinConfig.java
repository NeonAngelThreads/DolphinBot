package org.angellock.impl;

import lombok.Data;
import lombok.ToString;

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
    DebugSettings debugSettings;
    OtherSettings other;

    @Data
    @ToString
    public static class DebugSettings {
        boolean enablePacketDebug;
    }

    @Data
    @ToString
    public static class OtherSettings {
        boolean enableSkinRecorder;
    }

    public DolphinConfig mergeCommandOptions(Map<String, Object> commandLines) {
        for (Map.Entry<String, Object> opt : commandLines.entrySet()) {
            String value = (String) opt.getValue();
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
}
