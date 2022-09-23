// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;

public abstract class WstxInputSource
{
    protected final WstxInputSource mParent;
    protected final String mFromEntity;
    protected int mScopeId;
    protected int mEntityDepth;
    
    protected WstxInputSource(final WstxInputSource parent, final String fromEntity) {
        this.mScopeId = 0;
        this.mParent = parent;
        this.mFromEntity = fromEntity;
    }
    
    public abstract void overrideSource(final URL p0);
    
    public final WstxInputSource getParent() {
        return this.mParent;
    }
    
    public boolean isOrIsExpandedFrom(final String entityId) {
        if (entityId != null) {
            for (WstxInputSource curr = this; curr != null; curr = curr.mParent) {
                if (entityId == curr.mFromEntity) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public abstract boolean fromInternalEntity();
    
    public abstract URL getSource() throws IOException;
    
    public abstract String getPublicId();
    
    public abstract String getSystemId();
    
    protected abstract WstxInputLocation getLocation();
    
    public abstract WstxInputLocation getLocation(final long p0, final int p1, final int p2);
    
    public String getEntityId() {
        return this.mFromEntity;
    }
    
    public int getScopeId() {
        return this.mScopeId;
    }
    
    public int getEntityDepth() {
        return this.mEntityDepth;
    }
    
    public final void initInputLocation(final WstxInputData reader, final int currScopeId, final int entityDepth) {
        this.mScopeId = currScopeId;
        this.mEntityDepth = entityDepth;
        this.doInitInputLocation(reader);
    }
    
    protected abstract void doInitInputLocation(final WstxInputData p0);
    
    public abstract int readInto(final WstxInputData p0) throws IOException, XMLStreamException;
    
    public abstract boolean readMore(final WstxInputData p0, final int p1) throws IOException, XMLStreamException;
    
    public abstract void saveContext(final WstxInputData p0);
    
    public abstract void restoreContext(final WstxInputData p0);
    
    public abstract void close() throws IOException;
    
    public abstract void closeCompletely() throws IOException;
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(80);
        sb.append("<WstxInputSource [class ");
        sb.append(this.getClass().toString());
        sb.append("]; systemId: ");
        sb.append(this.getSystemId());
        sb.append(", source: ");
        try {
            final URL url = this.getSource();
            sb.append(url.toString());
        }
        catch (IOException e) {
            sb.append("[ERROR: " + e.getMessage() + "]");
        }
        sb.append('>');
        return sb.toString();
    }
}
