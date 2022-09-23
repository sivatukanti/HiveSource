// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import java.io.InputStream;

public class ConcatResourceInputStream extends InputStream
{
    private static final int EOF = -1;
    private boolean eof;
    private Iterator<Resource> iter;
    private InputStream currentStream;
    private ProjectComponent managingPc;
    private boolean ignoreErrors;
    
    public ConcatResourceInputStream(final ResourceCollection rc) {
        this.eof = false;
        this.ignoreErrors = false;
        this.iter = rc.iterator();
    }
    
    public void setIgnoreErrors(final boolean b) {
        this.ignoreErrors = b;
    }
    
    public boolean isIgnoreErrors() {
        return this.ignoreErrors;
    }
    
    @Override
    public void close() throws IOException {
        this.closeCurrent();
        this.eof = true;
    }
    
    @Override
    public int read() throws IOException {
        if (this.eof) {
            return -1;
        }
        int result = this.readCurrent();
        if (result == -1) {
            this.nextResource();
            result = this.readCurrent();
        }
        return result;
    }
    
    public void setManagingComponent(final ProjectComponent pc) {
        this.managingPc = pc;
    }
    
    public void log(final String message, final int loglevel) {
        if (this.managingPc != null) {
            this.managingPc.log(message, loglevel);
        }
        else {
            ((loglevel > 1) ? System.out : System.err).println(message);
        }
    }
    
    private int readCurrent() throws IOException {
        return (this.eof || this.currentStream == null) ? -1 : this.currentStream.read();
    }
    
    private void nextResource() throws IOException {
        this.closeCurrent();
        while (this.iter.hasNext()) {
            final Resource r = this.iter.next();
            if (!r.isExists()) {
                continue;
            }
            this.log("Concating " + r.toLongString(), 3);
            try {
                this.currentStream = new BufferedInputStream(r.getInputStream());
                return;
            }
            catch (IOException eyeOhEx) {
                if (!this.ignoreErrors) {
                    this.log("Failed to get input stream for " + r, 0);
                    throw eyeOhEx;
                }
                continue;
            }
            break;
        }
        this.eof = true;
    }
    
    private void closeCurrent() {
        FileUtils.close(this.currentStream);
        this.currentStream = null;
    }
}
