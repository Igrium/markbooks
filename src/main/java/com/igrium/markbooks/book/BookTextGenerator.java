package com.igrium.markbooks.book;

import java.util.Objects;
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

import com.igrium.markbooks.MarkBooksConfig;
import com.igrium.markbooks.util.StyleStack;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;

public class BookTextGenerator extends AbstractVisitor {

    /**
     * Create a book text generator from a given config.
     * @param config Config.
     * @param admin Whether the current player is an admin.
     * @return The book generator.
     */
    public static BookTextGenerator create(MarkBooksConfig config, boolean admin) {
        BookTextGenerator generator = new BookTextGenerator();
        switch (config.getAllowLinks()) {
            case ALWAYS:
                generator.setAllowLinks(true);
                break;
            case ADMINS:
                generator.setAllowLinks(admin);
                break;
            case NEVER:
                generator.setAllowLinks(false);
            default:
                break;
        }
        generator.setLinkColor(config.formatting().getLinkColor());

        TextColor[] headingColors = config.formatting().getHeadingColors();
        generator.setHeadingColorSupplier(i -> headingColors[MathHelper.clamp(i, 0, headingColors.length - 1)]);

        generator.setUlPrefix(config.formatting().getUlPrefix());
        generator.setOlPrefix(config.formatting().getOlPrefix());

        return generator;
    }

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

    public final boolean allowLinks() {
        return allowLinks;
    }

    public BookTextGenerator setAllowLinks(boolean allowLinks) {
        this.allowLinks = allowLinks;
        return this;
    }

    private IntFunction<TextColor> headingColorSupplier = l -> TextColor.fromRgb(0x990000);

    public BookTextGenerator setHeadingColorSupplier(IntFunction<TextColor> headingColorSupplier) {
        this.headingColorSupplier = Objects.requireNonNull(headingColorSupplier);
        return this;
    }

    private String ulPrefix = "• ";

    public final String getUlPrefix() {
        return ulPrefix;
    }

    public void setUlPrefix(String ulPrefix) {
        this.ulPrefix = Objects.requireNonNull(ulPrefix);
    }

    private String olPrefix = "%d. ";

    public final String getOlPrefix() {
        return olPrefix;
    }

    public void setOlPrefix(String olPrefix) {
        this.olPrefix = Objects.requireNonNull(olPrefix);
    }

    private TextColor linkColor = TextColor.fromRgb(0x8888FF);

    public TextColor getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(TextColor linkColor) {
        this.linkColor = linkColor;
    }

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
                    .withColor(linkColor)
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
                text.append(olPrefix.formatted(entry.index));
            } else {
                text.append(ulPrefix);
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
}
