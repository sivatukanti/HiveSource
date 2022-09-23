// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;

public abstract class BaseInputSource extends WstxInputSource
{
    final String mPublicId;
    SystemId mSystemId;
    protected char[] mBuffer;
    protected int mInputLast;
    long mSavedInputProcessed;
    int mSavedInputRow;
    int mSavedInputRowStart;
    int mSavedInputPtr;
    transient WstxInputLocation mParentLocation;
    
    protected BaseInputSource(final WstxInputSource parent, final String fromEntity, final String publicId, final SystemId systemId) {
        super(parent, fromEntity);
        this.mSavedInputProcessed = 0L;
        this.mSavedInputRow = 1;
        this.mSavedInputRowStart = 0;
        this.mSavedInputPtr = 0;
        this.mParentLocation = null;
        this.mSystemId = systemId;
        this.mPublicId = publicId;
    }
    
    @Override
    public void overrideSource(final URL src) {
        this.mSystemId = SystemId.construct(src);
    }
    
    @Override
    public abstract boolean fromInternalEntity();
    
    @Override
    public URL getSource() throws IOException {
        return (this.mSystemId == null) ? null : this.mSystemId.asURL();
    }
    
    @Override
    public String getPublicId() {
        return this.mPublicId;
    }
    
    @Override
    public String getSystemId() {
        return (this.mSystemId == null) ? null : this.mSystemId.toString();
    }
    
    @Override
    protected abstract void doInitInputLocation(final WstxInputData p0);
    
    @Override
    public abstract int readInto(final WstxInputData p0) throws IOException, XMLStreamException;
    
    @Override
    public abstract boolean readMore(final WstxInputData p0, final int p1) throws IOException, XMLStreamException;
    
    @Override
    public void saveContext(final WstxInputData reader) {
        this.mSavedInputPtr = reader.mInputPtr;
        this.mSavedInputProcessed = reader.mCurrInputProcessed;
        this.mSavedInputRow = reader.mCurrInputRow;
        this.mSavedInputRowStart = reader.mCurrInputRowStart;
    }
    
    @Override
    public void restoreContext(final WstxInputData reader) {
        reader.mInputBuffer = this.mBuffer;
        reader.mInputEnd = this.mInputLast;
        reader.mInputPtr = this.mSavedInputPtr;
        reader.mCurrInputProcessed = this.mSavedInputProcessed;
        reader.mCurrInputRow = this.mSavedInputRow;
        reader.mCurrInputRowStart = this.mSavedInputRowStart;
    }
    
    @Override
    public abstract void close() throws IOException;
    
    @Override
    protected final WstxInputLocation getLocation() {
        return this.getLocation(this.mSavedInputProcessed + this.mSavedInputPtr - 1L, this.mSavedInputRow, this.mSavedInputPtr - this.mSavedInputRowStart + 1);
    }
    
    @Override
    public final WstxInputLocation getLocation(final long total, final int row, final int col) {
        WstxInputLocation pl;
        if (this.mParent == null) {
            pl = null;
        }
        else {
            pl = this.mParentLocation;
            if (pl == null) {
                pl = (this.mParentLocation = this.mParent.getLocation());
            }
            pl = this.mParent.getLocation();
        }
        return new WstxInputLocation(pl, this.getPublicId(), this.getSystemId(), total, row, col);
    }
}
