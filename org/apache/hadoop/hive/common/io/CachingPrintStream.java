// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.List;
import java.io.PrintStream;

public class CachingPrintStream extends PrintStream
{
    List<String> output;
    
    public CachingPrintStream(final OutputStream out, final boolean autoFlush, final String encoding) throws FileNotFoundException, UnsupportedEncodingException {
        super(out, autoFlush, encoding);
        this.output = new ArrayList<String>();
    }
    
    public CachingPrintStream(final OutputStream out) {
        super(out);
        this.output = new ArrayList<String>();
    }
    
    @Override
    public void println(final String out) {
        this.output.add(out);
        super.println(out);
    }
    
    @Override
    public void flush() {
        this.output = new ArrayList<String>();
        super.flush();
    }
    
    public List<String> getOutput() {
        return this.output;
    }
}
