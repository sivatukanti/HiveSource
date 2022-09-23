// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

public interface CharStream
{
    char readChar() throws IOException;
    
    int getColumn();
    
    int getLine();
    
    int getEndColumn();
    
    int getEndLine();
    
    int getBeginColumn();
    
    int getBeginLine();
    
    void backup(final int p0);
    
    char BeginToken() throws IOException;
    
    String GetImage();
    
    char[] GetSuffix(final int p0);
    
    void Done();
    
    int getBeginOffset();
    
    int getEndOffset();
    
    void ReInit(final Reader p0, final int p1, final int p2, final int p3);
    
    void ReInit(final Reader p0, final int p1, final int p2);
    
    void ReInit(final InputStream p0, final int p1, final int p2, final int p3);
    
    void ReInit(final InputStream p0, final int p1, final int p2);
}
