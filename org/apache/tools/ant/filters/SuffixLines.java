// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import org.apache.tools.ant.types.Parameter;
import java.io.IOException;
import java.io.Reader;

public final class SuffixLines extends BaseParamFilterReader implements ChainableReader
{
    private static final String SUFFIX_KEY = "suffix";
    private String suffix;
    private String queuedData;
    
    public SuffixLines() {
        this.suffix = null;
        this.queuedData = null;
    }
    
    public SuffixLines(final Reader in) {
        super(in);
        this.suffix = null;
        this.queuedData = null;
    }
    
    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.queuedData != null && this.queuedData.length() == 0) {
            this.queuedData = null;
        }
        if (this.queuedData != null) {
            ch = this.queuedData.charAt(0);
            this.queuedData = this.queuedData.substring(1);
            if (this.queuedData.length() == 0) {
                this.queuedData = null;
            }
        }
        else {
            this.queuedData = this.readLine();
            if (this.queuedData != null) {
                if (this.suffix != null) {
                    String lf = "";
                    if (this.queuedData.endsWith("\r\n")) {
                        lf = "\r\n";
                    }
                    else if (this.queuedData.endsWith("\n")) {
                        lf = "\n";
                    }
                    this.queuedData = this.queuedData.substring(0, this.queuedData.length() - lf.length()) + this.suffix + lf;
                }
                return this.read();
            }
            ch = -1;
        }
        return ch;
    }
    
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
    
    private String getSuffix() {
        return this.suffix;
    }
    
    public Reader chain(final Reader rdr) {
        final SuffixLines newFilter = new SuffixLines(rdr);
        newFilter.setSuffix(this.getSuffix());
        newFilter.setInitialized(true);
        return newFilter;
    }
    
    private void initialize() {
        final Parameter[] params = this.getParameters();
        if (params != null) {
            for (int i = 0; i < params.length; ++i) {
                if ("suffix".equals(params[i].getName())) {
                    this.suffix = params[i].getValue();
                    break;
                }
            }
        }
    }
}
