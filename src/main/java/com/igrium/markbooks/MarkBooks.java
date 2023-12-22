package com.igrium.markbooks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.net.URISyntaxException;

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
    

    @Override
    public void onInitialize() {
        instance = this;
        CommandRegistrationCallback.EVENT.register(MarkBookCommand::register);
        try {
            filebinAPI = new FilebinAPI("https://filebin.net");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}