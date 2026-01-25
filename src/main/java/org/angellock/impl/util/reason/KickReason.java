package org.angellock.impl.util.reason;

public enum KickReason implements IReason {
    COMMAND_TOO_FAST(""),
    PLAYER_EXIST(""),
    HUMAN_VERIFICATION(""),
    CONNECTION_RESET(""),
    END_OF_STREAM("");

    private final String reason;

    KickReason(String reason) {
        this.reason = reason;
    }
}
