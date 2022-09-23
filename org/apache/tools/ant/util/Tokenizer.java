// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;

public interface Tokenizer
{
    String getToken(final Reader p0) throws IOException;
    
    String getPostToken();
}
