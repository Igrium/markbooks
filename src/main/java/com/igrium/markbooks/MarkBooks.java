package com.igrium.markbooks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.markbooks.command.MarkBookCommand;
import com.igrium.markbooks.filebin.FilebinAPI;

public class MarkBooks implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("markbooks");

    private static MarkBooks instance;

    public static MarkBooks getInstance() {
        return instance;
    }

    private FilebinAPI filebinAPI;

    public FilebinAPI getFilebinAPI() {
        return filebinAPI;
    }
    
    private MarkBooksConfig config;

    public MarkBooksConfig getConfig() {
        return config;
    }


    @Override
    public void onInitialize() {
        instance = this;
        initConfig();

        filebinAPI = new FilebinAPI(config.getFilebinUrl());
        CommandRegistrationCallback.EVENT.register(MarkBookCommand::register);
    }

    private void initConfig() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("markbooks.json");

        if (Files.isRegularFile(configFile)) {
            try (BufferedReader reader = Files.newBufferedReader(configFile)) {
                config = MarkBooksConfig.fromJson(reader);
            } catch (Exception e) {
                LOGGER.error("Error loading MarkBooks config.", e);
            }
        }

        if (config == null) {
            config = new MarkBooksConfig();
        }

        // Re-save config to reinit any missing keys
        try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
            writer.write(MarkBooksConfig.toJson(config));

        } catch (Exception e) {
            LOGGER.error("Error saving MarkBooks config.", e);
        }
    }
}