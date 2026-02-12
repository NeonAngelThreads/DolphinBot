package org.angellock.impl.managers;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProfileObject {
    private String profileName;
    private String name;
    private String password;
    protected List<String> owners = new ArrayList<>();
}
