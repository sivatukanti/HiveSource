// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.io.PrintStream;

public interface BuildLogger extends BuildListener
{
    void setMessageOutputLevel(final int p0);
    
    void setOutputPrintStream(final PrintStream p0);
    
    void setEmacsMode(final boolean p0);
    
    void setErrorPrintStream(final PrintStream p0);
}
