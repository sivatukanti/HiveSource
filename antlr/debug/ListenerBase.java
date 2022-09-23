// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import java.util.EventListener;

public interface ListenerBase extends EventListener
{
    void doneParsing(final TraceEvent p0);
    
    void refresh();
}
