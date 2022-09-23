// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import java.io.Writer;

final class DTDWriter
{
    final Writer mWriter;
    final boolean mIncludeComments;
    final boolean mIncludeConditionals;
    final boolean mIncludePEs;
    int mIsFlattening;
    int mFlattenStart;
    
    public DTDWriter(final Writer out, final boolean inclComments, final boolean inclCond, final boolean inclPEs) {
        this.mIsFlattening = 0;
        this.mFlattenStart = 0;
        this.mWriter = out;
        this.mIncludeComments = inclComments;
        this.mIncludeConditionals = inclCond;
        this.mIncludePEs = inclPEs;
        this.mIsFlattening = 1;
    }
    
    public boolean includeComments() {
        return this.mIncludeComments;
    }
    
    public boolean includeConditionals() {
        return this.mIncludeConditionals;
    }
    
    public boolean includeParamEntities() {
        return this.mIncludePEs;
    }
    
    public void disableOutput() {
        --this.mIsFlattening;
    }
    
    public void enableOutput(final int newStart) {
        ++this.mIsFlattening;
        this.mFlattenStart = newStart;
    }
    
    public void setFlattenStart(final int ptr) {
        this.mFlattenStart = ptr;
    }
    
    public int getFlattenStart() {
        return this.mFlattenStart;
    }
    
    public void flush(final char[] buf, final int upUntil) throws XMLStreamException {
        if (this.mFlattenStart < upUntil) {
            if (this.mIsFlattening > 0) {
                try {
                    this.mWriter.write(buf, this.mFlattenStart, upUntil - this.mFlattenStart);
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            this.mFlattenStart = upUntil;
        }
    }
    
    public void output(final String output) throws XMLStreamException {
        if (this.mIsFlattening > 0) {
            try {
                this.mWriter.write(output);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }
    
    public void output(final char c) throws XMLStreamException {
        if (this.mIsFlattening > 0) {
            try {
                this.mWriter.write(c);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }
}
