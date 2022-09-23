// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import org.apache.tools.ant.types.Parameter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.File;

public final class ConcatFilter extends BaseParamFilterReader implements ChainableReader
{
    private File prepend;
    private File append;
    private Reader prependReader;
    private Reader appendReader;
    
    public ConcatFilter() {
        this.prependReader = null;
        this.appendReader = null;
    }
    
    public ConcatFilter(final Reader in) {
        super(in);
        this.prependReader = null;
        this.appendReader = null;
    }
    
    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.prependReader != null) {
            ch = this.prependReader.read();
            if (ch == -1) {
                this.prependReader.close();
                this.prependReader = null;
            }
        }
        if (ch == -1) {
            ch = super.read();
        }
        if (ch == -1 && this.appendReader != null) {
            ch = this.appendReader.read();
            if (ch == -1) {
                this.appendReader.close();
                this.appendReader = null;
            }
        }
        return ch;
    }
    
    public void setPrepend(final File prepend) {
        this.prepend = prepend;
    }
    
    public File getPrepend() {
        return this.prepend;
    }
    
    public void setAppend(final File append) {
        this.append = append;
    }
    
    public File getAppend() {
        return this.append;
    }
    
    public Reader chain(final Reader rdr) {
        final ConcatFilter newFilter = new ConcatFilter(rdr);
        newFilter.setPrepend(this.getPrepend());
        newFilter.setAppend(this.getAppend());
        return newFilter;
    }
    
    private void initialize() throws IOException {
        final Parameter[] params = this.getParameters();
        if (params != null) {
            for (int i = 0; i < params.length; ++i) {
                if ("prepend".equals(params[i].getName())) {
                    this.setPrepend(new File(params[i].getValue()));
                }
                else if ("append".equals(params[i].getName())) {
                    this.setAppend(new File(params[i].getValue()));
                }
            }
        }
        if (this.prepend != null) {
            if (!this.prepend.isAbsolute()) {
                this.prepend = new File(this.getProject().getBaseDir(), this.prepend.getPath());
            }
            this.prependReader = new BufferedReader(new FileReader(this.prepend));
        }
        if (this.append != null) {
            if (!this.append.isAbsolute()) {
                this.append = new File(this.getProject().getBaseDir(), this.append.getPath());
            }
            this.appendReader = new BufferedReader(new FileReader(this.append));
        }
    }
}
