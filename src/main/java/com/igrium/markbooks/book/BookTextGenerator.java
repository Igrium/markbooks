package com.igrium.markbooks.book;

import java.util.Stack;
import java.util.function.IntFunction;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.Emphasis;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;

import com.igrium.markbooks.util.StyleStack;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class BookTextGenerator extends AbstractVisitor {
    private static class ListEntry {
        public ListEntry(boolean ordered) {
            this.ordered = ordered;
        }

        public final boolean ordered;

        public int index = 1;
    }

    private final MutableText text;
    private StyleStack stack = new StyleStack();
    private Stack<ListEntry> lists = new Stack<>();

    private boolean allowLinks;
    private IntFunction<TextColor> headingColorSupplier = l -> TextColor.fromRgb(0x990000);

    public BookTextGenerator() {
        this.text = Text.empty();
    }

    @Override
    public void visit(org.commonmark.node.Text node) {
        text.append(Text.literal(node.getLiteral()).setStyle(stack.peek()));
    }

    @Override
    public void visit(Emphasis emphasis) {
        stack.push(style -> style.withItalic(true));
        visitChildren(emphasis);
        stack.pop();
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        stack.push(style -> style.withBold(true));
        visitChildren(strongEmphasis);
        stack.pop();
    }

    @Override
    public void visit(Link link) {
        if (allowLinks()) {
            stack.push(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link.getDestination()))
                    .withColor(0x8888FF)
                    .withUnderline(true)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(link.getDestination()))));
        } else {
            stack.push(style -> style);
        }

        visitChildren(link);
        stack.pop();
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        text.append("\n");
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        text.append("\n");
    }

    @Override
    public void visit(Paragraph paragraph) {
        visitChildren(paragraph);
        text.append("\n\n");
    }
    
    @Override
    public void visit(ListItem listItem) {
        if (lists.isEmpty()) {
            visitChildren(listItem);
        } else {
            ListEntry entry = lists.peek();
            if (entry.ordered) {
                text.append(String.valueOf(entry.index) + ". ");
            } else {
                text.append("• ");
            }
            visitChildren(listItem);
            entry.index++;
        }
    }

    @Override
    public void visit(OrderedList orderedList) {
        // text.append("\n");
        lists.push(new ListEntry(true));
        visitChildren(orderedList);
        lists.pop();
    }

    @Override
    public void visit(BulletList bulletList) {
        // text.append("\n");
        lists.push(new ListEntry(false));
        visitChildren(bulletList);
        lists.pop();
    }

    @Override
    public void visit(Heading heading) {
        stack.push(style -> style.withBold(true)
                .withColor(headingColorSupplier.apply(heading.getLevel())));

        visitChildren(heading);
        stack.pop();
        text.append("\n\n");

    }

    public MutableText getText() {
        return text;
    }

    public boolean allowLinks() {
        return allowLinks;
    }

    public BookTextGenerator setAllowLinks(boolean allowLinks) {
        this.allowLinks = allowLinks;
        return this;
    }

    public BookTextGenerator setHeadingColorSupplier(IntFunction<TextColor> headingColorSupplier) {
        this.headingColorSupplier = headingColorSupplier;
        return this;
    }
}
