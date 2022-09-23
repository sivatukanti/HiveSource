// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import java.io.IOException;

public interface StringTemplateWriter
{
    public static final int NO_WRAP = -1;
    
    void pushIndentation(final String p0);
    
    String popIndentation();
    
    void pushAnchorPoint();
    
    void popAnchorPoint();
    
    void setLineWidth(final int p0);
    
    int write(final String p0) throws IOException;
    
    int write(final String p0, final String p1) throws IOException;
    
    int writeWrapSeparator(final String p0) throws IOException;
    
    int writeSeparator(final String p0) throws IOException;
}
