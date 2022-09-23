// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.stream;

import java.io.PrintWriter;

public interface HeaderPrintWriter
{
    void printlnWithHeader(final String p0);
    
    PrintWriterGetHeader getHeader();
    
    PrintWriter getPrintWriter();
    
    String getName();
    
    void print(final String p0);
    
    void println(final String p0);
    
    void println(final Object p0);
    
    void flush();
}
