package org.angellock.impl.managers.utils;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public interface IAccessible {
    JsonElement readDataFrom(Path filePath);
    void writeDataTo(HashMap<String, Object> data, Path filePath) throws IOException;
}
