// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import org.antlr.stringtemplate.StringTemplate;
import antlr.CommonAST;

public class StringTemplateAST extends CommonAST
{
    protected StringTemplate st;
    
    public StringTemplateAST() {
        this.st = null;
    }
    
    public StringTemplateAST(final int type, final String text) {
        this.st = null;
        this.setType(type);
        this.setText(text);
    }
    
    public StringTemplate getStringTemplate() {
        return this.st;
    }
    
    public void setStringTemplate(final StringTemplate st) {
        this.st = st;
    }
}
