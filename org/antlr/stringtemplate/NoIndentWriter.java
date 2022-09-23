// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import java.io.IOException;
import java.io.Writer;

public class NoIndentWriter extends AutoIndentWriter
{
    public NoIndentWriter(final Writer out) {
        super(out);
    }
    
    public int write(final String str) throws IOException {
        this.out.write(str);
        return str.length();
    }
}
