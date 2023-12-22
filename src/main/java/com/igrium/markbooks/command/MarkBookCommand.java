package com.igrium.markbooks.command;

import com.igrium.markbooks.MarkBooks;
import com.igrium.markbooks.book.BookGenerator;
import com.igrium.markbooks.filebin.FilebinException;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class MarkBookCommand {

    private static final SimpleCommandExceptionType BAD_URL = new SimpleCommandExceptionType(Text.literal("Invalid URL."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("markbook").then(
            literal("url").then(
                argument("url", StringArgumentType.string()).then(
                    argument("title", StringArgumentType.string()).executes(MarkBookCommand::createWithUrl)
                )
            )
        ).then(
            literal("filebin").then(
                argument("binId", StringArgumentType.string()).then(
                    argument("title", StringArgumentType.string()).executes(MarkBookCommand::createWithFilebin)
                )
            )
        ).then(
            literal("create").then(
                argument("title", StringArgumentType.greedyString()).executes(MarkBookCommand::prompt)
            )
        ));

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
        return create(context, new FilebinHandle(binId, MarkBooks.getInstance().getFilebinAPI()));
    }

    private static int create(CommandContext<ServerCommandSource> context, BookLoader loader) throws CommandSyntaxException {
        BookGenerator generator = new BookGenerator();
        String title = StringArgumentType.getString(context, "title");
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String author = player.getName().getString();
        
        context.getSource().sendFeedback(() -> Text.literal("Downloading..."), false);

        generator.writeBookAsync(loader, title, author).handleAsync((stack, e) -> {
            if (e != null) {
                e = e.getCause();
                if (e instanceof FilebinException) {
                    context.getSource().sendError(Text.literal("Unable to retrieve file from Filebin: " + e.getMessage()));
                } else {
                    LogUtils.getLogger().error("Error generating book.", e);
                    context.getSource().sendError(Text.literal("Error generating book: %s See console for details.".formatted(e.getCause().getMessage())));
                }

                return null;
            }

            if (requireWritableBook()) {
                int slotId = player.getInventory().selectedSlot;
                ItemStack prevStack = player.getInventory().getStack(slotId);
                
                if (!prevStack.isOf(Items.WRITABLE_BOOK)) {
                    context.getSource().sendError(Text.literal("Please hold a book and quill."));
                    return null;
                }

                player.getInventory().setStack(slotId, stack);
            } else {
                player.giveItemStack(stack);
            }

            context.getSource().sendFeedback(() -> Text.literal("Generated book: " + title), true);
                
            return null;
        }, context.getSource().getServer());

        return 1;
    }

    private static int prompt(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String title = StringArgumentType.getString(context, "title");

        UUID uuid = UUID.randomUUID();
        String uploadUrl = MarkBooks.getInstance().getFilebinAPI().getBin(uuid.toString()).toString();

        Text msg = Text.literal("Click ")
                .append(createUploadLink("this link", uploadUrl, "Click to open."))
                .append(" and upload your text file. ")
                .append("\n")
                .append("Once you've uploaded the file, hold a book and quill and click ")
                .append(createConfirmLink("here.", uuid.toString(), title, "Click to confirm upload."));
        
        context.getSource().sendFeedback(() -> msg, false);
        return 1;
    }

    private static Text createUploadLink(String text, String url, String hover) {
        return Text.literal(text).styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hover))))
                .formatted(Formatting.GREEN);

    }

    private static Text createConfirmLink(String text, String id, String title, String hover) {
        return Text.literal(text).styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/markbook filebin \"%s\" \"%s\"".formatted(id, title)))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hover))))
                .formatted(Formatting.GREEN);
    }

    private static boolean requireWritableBook() {
        return true;
    }
}
