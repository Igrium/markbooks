package com.igrium.markbooks.command;

import com.igrium.markbooks.book.BookProcessor;
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
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class BookTestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("booktest").then(
            argument("contents", StringArgumentType.greedyString()).executes(BookTestCommand::execute)
        ));
    }

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LogUtils.getLogger().info("Executing Command!");
        try {
            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
            stack = new BookProcessor().writeBook(stack, StringArgumentType.getString(context, "contents"), context.getSource().getName());
            context.getSource().getPlayerOrThrow().getInventory().insertStack(stack);

            context.getSource().sendFeedback(() -> Text.literal("Created book."), false);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error writing book.", e);
            throw new SimpleCommandExceptionType(Text.literal("An error occurd creating the book. See console for details.")).create();
        }
        return 1;
    }

}
