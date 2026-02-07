package org.angellock.impl.managers;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProfileObject {

    private String password;
    private String name;
    private String profileName;
    protected List<String> owners = new ArrayList<>();

    protected String server;
    protected int port;

    protected long ReconnectionDelay;
    protected int TIME_OUT;

}
