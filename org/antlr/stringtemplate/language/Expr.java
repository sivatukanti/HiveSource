// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.io.IOException;
import org.antlr.stringtemplate.StringTemplateWriter;
import org.antlr.stringtemplate.StringTemplate;

public abstract class Expr
{
    protected StringTemplate enclosingTemplate;
    protected String indentation;
    
    public Expr(final StringTemplate enclosingTemplate) {
        this.indentation = null;
        this.enclosingTemplate = enclosingTemplate;
    }
    
    public abstract int write(final StringTemplate p0, final StringTemplateWriter p1) throws IOException;
    
    public StringTemplate getEnclosingTemplate() {
        return this.enclosingTemplate;
    }
    
    public String getIndentation() {
        return this.indentation;
    }
    
    public void setIndentation(final String indentation) {
        this.indentation = indentation;
    }
}
