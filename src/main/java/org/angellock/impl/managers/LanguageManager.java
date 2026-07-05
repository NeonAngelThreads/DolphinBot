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

package org.angellock.impl.managers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.angellock.impl.Start;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageManager {
    private static final Logger log = LoggerFactory.getLogger(LanguageManager.class);
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();
    private static final Map<String, JsonObject> LANGUAGE_CACHE = new HashMap<>();
    private static final String LANG_RESOURCE_PATH = "lang/";
    private static final String DEFAULT_LANG = "en_us";

    private static final Map<String, String> LANGUAGE_FILENAME_MAP = new HashMap<>();
    private static File langDirectory;

    static {
        LANGUAGE_FILENAME_MAP.put("zh", "zh_cn.json");
        LANGUAGE_FILENAME_MAP.put("en", "en_us.json");
        initializeLangDirectory();
    }

    private static void initializeLangDirectory() {
        String basePath = new Manager().getBaseConfigRoot();
        langDirectory = new File(basePath, "lang");
        
        if (!langDirectory.exists()) {
            langDirectory.mkdirs();
        }
        
        for (String fileName : LANGUAGE_FILENAME_MAP.values()) {
            autoCopyLangFile(fileName);
        }
    }

    private static void autoCopyLangFile(String fileName) {
        File outFile = new File(langDirectory, fileName);
        if (!outFile.exists()) {
            InputStream in = Start.class.getClassLoader().getResourceAsStream(LANG_RESOURCE_PATH + fileName);
            if (in != null) {
                try {
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    log.info(ConsoleTokens.colorizeText("&9Extract language file &3." + File.separator + "lang" + File.separator + fileName));
                } catch (IOException e) {
                    log.error(ConsoleTokens.colorizeText("&cFailed to extract language file: " + fileName), e);
                }
            } else {
                log.warn(ConsoleTokens.colorizeText("&eCould not find language file in resources: " + fileName));
            }
        }
    }

    public static @NotNull String translate(@NotNull String key, Object... args) {
        String language = ConfigManager.global().getLanguage().getLanguage();
        JsonObject langData = getLanguageData(language);
        String translation = langData.has(key) ? langData.get(key).getAsString() : key;
        return formatTranslation(translation, args);
    }

    public static void registerLanguage(String languageCode, String fileName) {
        LANGUAGE_FILENAME_MAP.put(languageCode.toLowerCase(Locale.ROOT), fileName);
        autoCopyLangFile(fileName);
    }

    private static JsonObject getLanguageData(@NotNull String language) {
        String lowerLang = language.toLowerCase(Locale.ROOT);
        
        if (LANGUAGE_CACHE.containsKey(lowerLang)) {
            return LANGUAGE_CACHE.get(lowerLang);
        }

        String fileName = LANGUAGE_FILENAME_MAP.getOrDefault(lowerLang, DEFAULT_LANG + ".json");
        JsonObject langData = loadLanguageFile(fileName);
        
        if (langData == null || langData.size() == 0) {
            fileName = DEFAULT_LANG + ".json";
            langData = loadLanguageFile(fileName);
        }

        if (langData != null) {
            LANGUAGE_CACHE.put(lowerLang, langData);
        }
        
        return langData != null ? langData : new JsonObject();
    }

    private static @Nullable JsonObject loadLanguageFile(String fileName) {
        File langFile = new File(langDirectory, fileName);
        
        if (!langFile.exists()) {
            return null;
        }

        try {
            BufferedReader reader = Files.newBufferedReader(langFile.toPath(), StandardCharsets.UTF_8);
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            log.error(ConsoleTokens.colorizeText("&cFailed to load language file: " + fileName), e);
            return null;
        }
    }

    private static @NotNull String formatTranslation(@NotNull String translation, Object... args) {
        if (args == null || args.length == 0) {
            return translation;
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int placeholderIndex;

        while ((placeholderIndex = translation.indexOf("%s")) != -1) {
            result.append(translation, 0, placeholderIndex);
            if (argIndex < args.length) {
                result.append(args[argIndex++]);
            } else {
                result.append("%s");
            }
            translation = translation.substring(placeholderIndex + 2);
        }
        result.append(translation);
        return result.toString();
    }

    public static File getLangDirectory() {
        return langDirectory;
    }
}
