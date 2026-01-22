package org.angellock.impl.managers.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJsonAccessor extends Manager implements IAccessible{
    protected Gson Helper = new Gson();
    protected Path configPath;


//    public Path getDataFilePath() {
//        return Paths.get((this.configPath != null) ? this.configPath : (super.getBaseConfigRoot() + getFileName() + ".json"));
//    }

    public abstract String getFileName();
    @Override
    public Map<String, JsonElement> readDataFrom(Path filePath) {
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
        Gson Helper = (new GsonBuilder()).create();
        return Helper.fromJson(reader, JsonObject.class).asMap();
    }

    public Map<String, JsonElement> readJSONContent(){
        return this.readDataFrom(this.configPath);
    }
    @Override
    public void writeDataTo(HashMap<String, Object> data, Path filePath) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath.toString()), StandardCharsets.UTF_8);
        osw.write(this.Helper.toJson(data));

        osw.flush();
        osw.close();
    }

    public void writeContentAsJson(HashMap<String, Object> data) throws IOException {
        writeDataTo(data, this.configPath);
    }
}
