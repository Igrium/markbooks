package com.igrium.markbooks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.markbooks.command.MarkBooksCommand;
import com.igrium.markbooks.filebin.FilebinAPI;

public class MarkBooks implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("markbooks");

    public static FilebinAPI FILEBIN;

    @Override
    public void onInitialize() {
        // CommandRegistrationCallback.EVENT.register(BookTestCommand::register);
        CommandRegistrationCallback.EVENT.register(MarkBooksCommand::register);
        try {
            FILEBIN = new FilebinAPI("https://filebin.net");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}