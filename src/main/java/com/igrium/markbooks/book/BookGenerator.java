package com.igrium.markbooks.book;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.igrium.markbooks.loader.BookLoader;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

public class BookGenerator {

    private final Parser parser = Parser.builder().build();
    
    public CompletableFuture<ItemStack> writeBookAsync(BookLoader loader, String title, String author) {
        // We shouldn't be messing with an existing book off-thread.
        return BookLoader.loadAsync(loader).thenApply(
            md -> writeBook(new ItemStack(Items.WRITTEN_BOOK), md, title, author)
        );
    }

    public ItemStack writeBook(ItemStack stack, String markdown, String title, String author) {
        BookTextGenerator generator = new BookTextGenerator();
        Node document = parser.parse(markdown);
        document.accept(generator);

        return writeBookNbt(stack, List.of(generator.getText()), title, author);
    }

    public ItemStack writeBookNbt(ItemStack stack, List<Text> pages, String title, String author) {
        if (!stack.isOf(Items.WRITTEN_BOOK)) return stack;

        stack.setSubNbt("author", NbtString.of(author));
        stack.setSubNbt("title", NbtString.of(title));

        NbtList pageList = new NbtList();
        for (Text page : pages) {
            pageList.add(NbtString.of(Text.Serializer.toJson(page)));
        }

        stack.setSubNbt("pages", pageList);

        return stack;
    }
}
