package com.igrium.markbooks.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.text.TextColor;

public class TextColorJsonAdapter extends TypeAdapter<TextColor> {

    @Override
    public TextColor read(JsonReader reader) throws IOException {
        String val = reader.nextString();
        TextColor color = TextColor.parse(val);
        if (color == null) {
            throw new IOException("String is not a valid color name or hex color code");
        }
        return color;
    }

    @Override
    public void write(JsonWriter writer, TextColor color) throws IOException {
        writer.value(color.getName());
    }
    
}
