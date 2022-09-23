// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

public class FileTokenizer extends ProjectComponent implements Tokenizer
{
    public String getToken(final Reader in) throws IOException {
        return FileUtils.readFully(in);
    }
    
    public String getPostToken() {
        return "";
    }
}
