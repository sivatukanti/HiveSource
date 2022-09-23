// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

public class UniqFilter extends TokenFilter.ChainableReaderFilter
{
    private String lastLine;
    
    public UniqFilter() {
        this.lastLine = null;
    }
    
    public String filter(final String string) {
        String s;
        if (this.lastLine == null || !this.lastLine.equals(string)) {
            s = string;
            this.lastLine = string;
        }
        else {
            s = null;
        }
        return s;
    }
}
