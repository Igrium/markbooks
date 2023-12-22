package com.igrium.markbooks.command;

import com.igrium.markbooks.MarkBooks;
import com.igrium.markbooks.book.BookGenerator;
import com.igrium.markbooks.filebin.FilebinHandle;
import com.igrium.markbooks.loader.BookLoader;
import com.igrium.markbooks.loader.UrlBookLoader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

import java.net.MalformedURLException;
import java.net.URL;

public class MarkBooksCommand {

    private static final SimpleCommandExceptionType BAD_URL = new SimpleCommandExceptionType(Text.literal("Invalid URL."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("markbook").then(
            argument("title", StringArgumentType.string()).then(
                literal("url").then(
                    argument("url", StringArgumentType.string()).executes(MarkBooksCommand::createWithUrl))
                ).then(
                    literal("filebin").then(
                        argument("binId", StringArgumentType.string()).executes(MarkBooksCommand::createWithFilebin)
                    )
                )
            )
        );
    }

    private static int createWithUrl(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String urlStr = StringArgumentType.getString(context, "url");
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            LogUtils.getLogger().error("bad URL", e);
            throw BAD_URL.create();
        }

        return create(context, new UrlBookLoader(url));
    }

    private static int createWithFilebin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String binId = StringArgumentType.getString(context, "binId");
        return create(context, new FilebinHandle(binId, MarkBooks.FILEBIN));
    }

    private static int create(CommandContext<ServerCommandSource> context, BookLoader loader) throws CommandSyntaxException {
        BookGenerator generator = new BookGenerator();
        String title = StringArgumentType.getString(context, "title");
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String author = player.getName().getString();

        generator.writeBookAsync(loader, title, author).handleAsync((stack, e) -> {
            if (e != null) {
                LogUtils.getLogger().error("Error generating book.", e);
                context.getSource().sendError(Text.literal("Error generating book: %s See console for details.".formatted(e.getMessage())));
                return null;
            }

            player.giveItemStack(stack);
            context.getSource().sendFeedback(() -> Text.literal("Generated book: " + title), true);
                
            return null;
        }, context.getSource().getServer());

        return 1;
    }
    
}
