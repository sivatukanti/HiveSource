// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;

public class CommonGroupLoader extends PathGroupLoader
{
    public CommonGroupLoader(final StringTemplateErrorListener errors) {
        super(errors);
    }
    
    public CommonGroupLoader(final String dirStr, final StringTemplateErrorListener errors) {
        super(dirStr, errors);
    }
    
    protected BufferedReader locate(final String name) throws IOException {
        for (int i = 0; i < this.dirs.size(); ++i) {
            final String dir = this.dirs.get(i);
            final String fileName = dir + "/" + name;
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream(fileName);
            if (is == null) {
                cl = this.getClass().getClassLoader();
                is = cl.getResourceAsStream(fileName);
            }
            if (is != null) {
                return new BufferedReader(this.getInputStreamReader(is));
            }
        }
        return null;
    }
}
