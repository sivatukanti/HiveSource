// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class FetchConverter extends PrintStream
{
    protected volatile boolean queryfound;
    protected volatile boolean fetchStarted;
    
    public FetchConverter(final OutputStream out, final boolean autoFlush, final String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }
    
    public void foundQuery(final boolean queryfound) {
        this.queryfound = queryfound;
    }
    
    public void fetchStarted() {
        this.fetchStarted = true;
    }
    
    @Override
    public void println(final String out) {
        if (this.byPass()) {
            this.printDirect(out);
        }
        else {
            this.process(out);
        }
    }
    
    protected final void printDirect(final String out) {
        super.println(out);
    }
    
    protected final boolean byPass() {
        return !this.queryfound || !this.fetchStarted;
    }
    
    protected abstract void process(final String p0);
    
    protected abstract void processFinal();
    
    @Override
    public void flush() {
        if (this.byPass()) {
            super.flush();
        }
    }
    
    public void fetchFinished() {
        if (!this.byPass()) {
            this.processFinal();
        }
        super.flush();
        this.fetchStarted = false;
    }
}
