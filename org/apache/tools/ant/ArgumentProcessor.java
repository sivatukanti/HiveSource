// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.io.PrintStream;
import java.util.List;

public interface ArgumentProcessor
{
    int readArguments(final String[] p0, final int p1);
    
    boolean handleArg(final List<String> p0);
    
    void prepareConfigure(final Project p0, final List<String> p1);
    
    boolean handleArg(final Project p0, final List<String> p1);
    
    void printUsage(final PrintStream p0);
}
