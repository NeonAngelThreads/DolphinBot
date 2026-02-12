package org.angellock.impl.managers.utils;

import com.google.gson.*;

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
    protected final Gson Helper;
    protected Path configPath;

    public AbstractJsonAccessor() {
        Helper = (new GsonBuilder())
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create();
    }

    public abstract String getFileName();
    @Override
    public JsonObject readDataFrom(Path filePath) {
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
        return this.Helper.fromJson(reader, JsonObject.class);
    }

    public Map<String, JsonElement> readJSONContent(){
        return this.readDataFrom(this.configPath).asMap();
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
