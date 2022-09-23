// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.io.IOException;
import org.antlr.stringtemplate.StringTemplateWriter;
import org.antlr.stringtemplate.StringTemplate;

public class StringRef extends Expr
{
    String str;
    
    public StringRef(final StringTemplate enclosingTemplate, final String str) {
        super(enclosingTemplate);
        this.str = str;
    }
    
    public int write(final StringTemplate self, final StringTemplateWriter out) throws IOException {
        if (this.str != null) {
            final int n = out.write(this.str);
            return n;
        }
        return 0;
    }
    
    public String toString() {
        if (this.str != null) {
            return this.str;
        }
        return "";
    }
}
