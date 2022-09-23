// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import org.apache.tools.ant.types.Parameter;
import java.io.IOException;
import java.io.Reader;

public final class StripLineBreaks extends BaseParamFilterReader implements ChainableReader
{
    private static final String DEFAULT_LINE_BREAKS = "\r\n";
    private static final String LINE_BREAKS_KEY = "linebreaks";
    private String lineBreaks;
    
    public StripLineBreaks() {
        this.lineBreaks = "\r\n";
    }
    
    public StripLineBreaks(final Reader in) {
        super(in);
        this.lineBreaks = "\r\n";
    }
    
    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch;
        for (ch = this.in.read(); ch != -1 && this.lineBreaks.indexOf(ch) != -1; ch = this.in.read()) {}
        return ch;
    }
    
    public void setLineBreaks(final String lineBreaks) {
        this.lineBreaks = lineBreaks;
    }
    
    private String getLineBreaks() {
        return this.lineBreaks;
    }
    
    public Reader chain(final Reader rdr) {
        final StripLineBreaks newFilter = new StripLineBreaks(rdr);
        newFilter.setLineBreaks(this.getLineBreaks());
        newFilter.setInitialized(true);
        return newFilter;
    }
    
    private void initialize() {
        String userDefinedLineBreaks = null;
        final Parameter[] params = this.getParameters();
        if (params != null) {
            for (int i = 0; i < params.length; ++i) {
                if ("linebreaks".equals(params[i].getName())) {
                    userDefinedLineBreaks = params[i].getValue();
                    break;
                }
            }
        }
        if (userDefinedLineBreaks != null) {
            this.lineBreaks = userDefinedLineBreaks;
        }
    }
}
