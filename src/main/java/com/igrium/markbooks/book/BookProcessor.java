package com.igrium.markbooks.book;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

public class BookProcessor {

    private final Parser parser = Parser.builder().build();

    public ItemStack writeBook(ItemStack stack, Text contents) {
        if (!stack.isOf(Items.WRITTEN_BOOK)) {
            return stack;
        }

        NbtList list = new NbtList();
        list.add(NbtString.of(Text.Serializer.toJson(contents)));

        NbtCompound compound = new NbtCompound();
        compound.put("pages", list);
        stack.setNbt(compound);
        return stack;
    }

    public ItemStack writeBook(ItemStack stack, String markdown) {
        BookTextGenerator generator = new BookTextGenerator();
        Node document = parser.parse(markdown);

        document.accept(generator);
        
        return writeBook(stack, generator.getText());
    }
}
