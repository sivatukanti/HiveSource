// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import com.ctc.wstx.util.StringUtil;
import org.codehaus.stax2.XMLStreamLocation2;
import java.io.Serializable;

public class WstxInputLocation implements Serializable, XMLStreamLocation2
{
    private static final long serialVersionUID = 1L;
    private static final WstxInputLocation sEmptyLocation;
    protected final WstxInputLocation mContext;
    protected final String mPublicId;
    protected final String mSystemId;
    protected final long mCharOffset;
    protected final int mCol;
    protected final int mRow;
    protected transient String mDesc;
    
    public WstxInputLocation(final WstxInputLocation ctxt, final String pubId, final String sysId, final long charOffset, final int row, final int col) {
        this.mDesc = null;
        this.mContext = ctxt;
        this.mPublicId = pubId;
        this.mSystemId = sysId;
        this.mCharOffset = charOffset;
        this.mCol = col;
        this.mRow = row;
    }
    
    public WstxInputLocation(final WstxInputLocation ctxt, final String pubId, final SystemId sysId, final long charOffset, final int row, final int col) {
        this.mDesc = null;
        this.mContext = ctxt;
        this.mPublicId = pubId;
        this.mSystemId = ((sysId == null) ? "N/A" : sysId.toString());
        this.mCharOffset = charOffset;
        this.mCol = col;
        this.mRow = row;
    }
    
    public static WstxInputLocation getEmptyLocation() {
        return WstxInputLocation.sEmptyLocation;
    }
    
    public long getCharacterOffsetLong() {
        return this.mCharOffset;
    }
    
    @Override
    public int getCharacterOffset() {
        return (int)this.mCharOffset;
    }
    
    @Override
    public int getColumnNumber() {
        return this.mCol;
    }
    
    @Override
    public int getLineNumber() {
        return this.mRow;
    }
    
    @Override
    public String getPublicId() {
        return this.mPublicId;
    }
    
    @Override
    public String getSystemId() {
        return this.mSystemId;
    }
    
    @Override
    public XMLStreamLocation2 getContext() {
        return this.mContext;
    }
    
    @Override
    public String toString() {
        if (this.mDesc == null) {
            StringBuilder sb;
            if (this.mContext != null) {
                sb = new StringBuilder(200);
            }
            else {
                sb = new StringBuilder(80);
            }
            this.appendDesc(sb);
            this.mDesc = sb.toString();
        }
        return this.mDesc;
    }
    
    @Override
    public int hashCode() {
        return (int)this.mCharOffset ^ (int)(-1L & this.mCharOffset >> 32) ^ this.mRow ^ this.mCol + (this.mCol << 3);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof WstxInputLocation)) {
            return false;
        }
        final WstxInputLocation other = (WstxInputLocation)o;
        if (other.getCharacterOffsetLong() != this.getCharacterOffsetLong()) {
            return false;
        }
        String otherPub = other.getPublicId();
        if (otherPub == null) {
            otherPub = "";
        }
        if (!otherPub.equals(this.mPublicId)) {
            return false;
        }
        String otherSys = other.getSystemId();
        if (otherSys == null) {
            otherSys = "";
        }
        return otherSys.equals(this.mSystemId);
    }
    
    private void appendDesc(final StringBuilder sb) {
        String srcId;
        if (this.mSystemId != null) {
            sb.append("[row,col,system-id]: ");
            srcId = this.mSystemId;
        }
        else if (this.mPublicId != null) {
            sb.append("[row,col,public-id]: ");
            srcId = this.mPublicId;
        }
        else {
            sb.append("[row,col {unknown-source}]: ");
            srcId = null;
        }
        sb.append('[');
        sb.append(this.mRow);
        sb.append(',');
        sb.append(this.mCol);
        if (srcId != null) {
            sb.append(',');
            sb.append('\"');
            sb.append(srcId);
            sb.append('\"');
        }
        sb.append(']');
        if (this.mContext != null) {
            StringUtil.appendLF(sb);
            sb.append(" from ");
            this.mContext.appendDesc(sb);
        }
    }
    
    static {
        sEmptyLocation = new WstxInputLocation(null, "", "", -1L, -1, -1);
    }
}
