// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.List;
import antlr.CommonToken;

public class StringTemplateToken extends CommonToken
{
    public List args;
    
    public StringTemplateToken() {
    }
    
    public StringTemplateToken(final int type, final String text) {
        super(type, text);
    }
    
    public StringTemplateToken(final String text) {
        super(text);
    }
    
    public StringTemplateToken(final int type, final String text, final List args) {
        super(type, text);
        this.args = args;
    }
    
    public String toString() {
        return super.toString() + "; args=" + this.args;
    }
}
