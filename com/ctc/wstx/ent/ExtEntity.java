// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.ent;

import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.api.ReaderConfig;
import javax.xml.stream.XMLResolver;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;

public abstract class ExtEntity extends EntityDecl
{
    final String mPublicId;
    final String mSystemId;
    
    public ExtEntity(final Location loc, final String name, final URL ctxt, final String pubId, final String sysId) {
        super(loc, name, ctxt);
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }
    
    @Override
    public abstract String getNotationName();
    
    @Override
    public String getPublicId() {
        return this.mPublicId;
    }
    
    @Override
    public String getReplacementText() {
        return null;
    }
    
    @Override
    public int getReplacementText(final Writer w) {
        return 0;
    }
    
    @Override
    public String getSystemId() {
        return this.mSystemId;
    }
    
    @Override
    public abstract void writeEnc(final Writer p0) throws IOException;
    
    @Override
    public char[] getReplacementChars() {
        return null;
    }
    
    @Override
    public boolean isExternal() {
        return true;
    }
    
    @Override
    public abstract boolean isParsed();
    
    @Override
    public abstract WstxInputSource expand(final WstxInputSource p0, final XMLResolver p1, final ReaderConfig p2, final int p3) throws IOException, XMLStreamException;
}
