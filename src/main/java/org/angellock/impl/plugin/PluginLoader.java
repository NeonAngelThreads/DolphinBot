package org.angellock.impl.plugin;

import com.google.gson.Gson;
import org.angellock.impl.extensions.BaseDefaultPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader{
    private static final Logger log = LoggerFactory.getLogger(PluginLoader.class);
    private final Gson gson = new Gson();

    public Plugin loadDefaultPlugin(){
        return new BaseDefaultPlugin();
    }
    public @Nullable Plugin loadPluginClass(File pluginFile){
        Manifest pluginManifest = this.getManifestOf(pluginFile);
        if (pluginManifest == null){
            return null;
        }
        try {
            log.info(ConsoleTokens.colorizeText("Loading plugin: &8{}"), pluginManifest);
            URL[] urls = new URL[]{
                    pluginFile.toURI().toURL()
            };
            URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader());
            Class<?> jarClass;
            try {
                jarClass = Class.forName(pluginManifest.getMainClass(), true, classLoader);

                if(Plugin.class.isAssignableFrom(jarClass)){
//                    classLoader.close();
                    Constructor<?> constructor = jarClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    return (Plugin) constructor.newInstance();
                }

            } catch (ClassNotFoundException var11) {
                log.warn(ConsoleTokens.colorizeText("&6Cannot find entry class '" + pluginManifest.getMainClass() + "'" + var11));
                log.warn(ConsoleTokens.colorizeText("&6Trying to load fallback entry class &d'Plugin.class'"));
                try {
                    ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class, classLoader);
                    if(serviceLoader.findFirst().isPresent()){
                        return serviceLoader.findFirst().get();
                    }
                    else {
                        log.error(ConsoleTokens.colorizeText("&4Failed to load plugin: " + pluginManifest));
                    }
                } catch (NoClassDefFoundError e) {
                    log.error(ConsoleTokens.colorizeText("&4Failed to load plugin: " + pluginManifest));
                    log.error(ConsoleTokens.colorizeText("&7" + e.toString()));
                }
            } finally {
                try {
                    classLoader.close();
                } catch (IOException ignored) {
                }
            }

//            Class<?> pluginClass;
//            try {
//                pluginClass = jarClass.asSubclass(Plugin.class);
//            } catch (ClassCastException var10) {
//                //throw new InvalidPluginException("main class `" + description.getMain() + "' does not extend JavaPlugin", var10);
//            }
//
//            this.plugin = (Plugin)new pluginClass;

        } catch (IllegalAccessException var12) {
            log.error(ConsoleTokens.colorizeText("&4Error loading plugin: Plugin " +pluginManifest+ "has no public constructor"));
            log.error(ConsoleTokens.colorizeText("&7{}"), var12.toString());
        } catch (MalformedURLException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            log.error(ConsoleTokens.colorizeText("&4Failed to load plugin " +
                    pluginManifest + "&c No such entry class named &l&5"+ pluginManifest.getMainClass())
            );

            log.error(ConsoleTokens.standardizeText(ConsoleTokens.GRAY + e.toString()));
        }
        return null;
    }

    public @Nullable Manifest getManifestOf(File plugin){

        if(plugin != null) {
            JarFile jar = null;
            InputStream stream = null;
            Manifest manifest = null;
            try {
                jar = new JarFile(plugin);
                JarEntry entry = jar.getJarEntry("plugin.json");
                if (entry == null) {
                    log.error(ConsoleTokens.colorizeText("&4The jar file should either specified Main class as Plugin.class or define a custom class name in plugin.json"));
                }

                stream = jar.getInputStream(entry);
                manifest = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Manifest.class);

            } catch (IOException ignored) {
                log.error(ConsoleTokens.colorizeText("&4An error occurred: IOException"));
            }
            finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException ignored) {
                        log.error(ConsoleTokens.colorizeText("&6An error occurred: IOException"));
                    }
                }

                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                        log.error(ConsoleTokens.colorizeText("&6An error occurred: IOException"));
                    }
                }
            }
            return manifest;
        }
        return null;
    }
}
