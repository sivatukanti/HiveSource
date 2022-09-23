// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.io.IOException;
import java.util.List;

public interface CompletionHandler
{
    boolean complete(final ConsoleReader p0, final List p1, final int p2) throws IOException;
}
