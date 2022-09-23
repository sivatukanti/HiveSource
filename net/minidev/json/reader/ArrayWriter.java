// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.reader;

import java.io.IOException;
import net.minidev.json.JSONValue;
import net.minidev.json.JSONStyle;

public class ArrayWriter implements JsonWriterI<Object>
{
    @Override
    public <E> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
        compression.arrayStart(out);
        boolean needSep = false;
        Object[] array;
        for (int length = (array = (Object[])(Object)value).length, i = 0; i < length; ++i) {
            final Object o = array[i];
            if (needSep) {
                compression.objectNext(out);
            }
            else {
                needSep = true;
            }
            JSONValue.writeJSONString(o, out, compression);
        }
        compression.arrayStop(out);
    }
}
