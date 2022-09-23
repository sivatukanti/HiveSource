// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.reader;

import java.io.IOException;
import net.minidev.json.JSONStyle;

public interface JsonWriterI<T>
{
     <E extends T> void writeJSONString(final E p0, final Appendable p1, final JSONStyle p2) throws IOException;
}
