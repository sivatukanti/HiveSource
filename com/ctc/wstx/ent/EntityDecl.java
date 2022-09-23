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
import javax.xml.stream.Location;
import java.net.URL;
import com.ctc.wstx.evt.WEntityDeclaration;

public abstract class EntityDecl extends WEntityDeclaration
{
    final String mName;
    final URL mContext;
    protected boolean mDeclaredExternally;
    
    public EntityDecl(final Location loc, final String name, final URL ctxt) {
        super(loc);
        this.mDeclaredExternally = false;
        this.mName = name;
        this.mContext = ctxt;
    }
    
    public void markAsExternallyDeclared() {
        this.mDeclaredExternally = true;
    }
    
    @Override
    public final String getBaseURI() {
        return this.mContext.toExternalForm();
    }
    
    @Override
    public final String getName() {
        return this.mName;
    }
    
    @Override
    public final Location getLocation() {
        return this.mLocation;
    }
    
    @Override
    public abstract String getNotationName();
    
    @Override
    public abstract String getPublicId();
    
    @Override
    public abstract String getReplacementText();
    
    public abstract int getReplacementText(final Writer p0) throws IOException;
    
    @Override
    public abstract String getSystemId();
    
    public boolean wasDeclaredExternally() {
        return this.mDeclaredExternally;
    }
    
    @Override
    public abstract void writeEnc(final Writer p0) throws IOException;
    
    public abstract char[] getReplacementChars();
    
    public final int getReplacementTextLength() {
        final String str = this.getReplacementText();
        return (str == null) ? 0 : str.length();
    }
    
    public abstract boolean isExternal();
    
    public abstract boolean isParsed();
    
    public abstract WstxInputSource expand(final WstxInputSource p0, final XMLResolver p1, final ReaderConfig p2, final int p3) throws IOException, XMLStreamException;
}
