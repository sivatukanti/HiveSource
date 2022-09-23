// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import com.ctc.wstx.cfg.ErrorConsts;
import javax.xml.stream.Location;

public final class ElementId
{
    private boolean mDefined;
    private final String mIdValue;
    private Location mLocation;
    private PrefixedName mElemName;
    private PrefixedName mAttrName;
    private ElementId mNextUndefined;
    private ElementId mNextColl;
    
    ElementId(final String id, final Location loc, final boolean defined, final PrefixedName elemName, final PrefixedName attrName) {
        this.mIdValue = id;
        this.mLocation = loc;
        this.mDefined = defined;
        this.mElemName = elemName;
        this.mAttrName = attrName;
    }
    
    protected void linkUndefined(final ElementId undefined) {
        if (this.mNextUndefined != null) {
            throw new IllegalStateException("ElementId '" + this + "' already had net undefined set ('" + this.mNextUndefined + "')");
        }
        this.mNextUndefined = undefined;
    }
    
    protected void setNextColliding(final ElementId nextColl) {
        this.mNextColl = nextColl;
    }
    
    public String getId() {
        return this.mIdValue;
    }
    
    public Location getLocation() {
        return this.mLocation;
    }
    
    public PrefixedName getElemName() {
        return this.mElemName;
    }
    
    public PrefixedName getAttrName() {
        return this.mAttrName;
    }
    
    public boolean isDefined() {
        return this.mDefined;
    }
    
    public boolean idMatches(final char[] buf, int start, int len) {
        if (this.mIdValue.length() != len) {
            return false;
        }
        if (buf[start] != this.mIdValue.charAt(0)) {
            return false;
        }
        int i = 1;
        len += start;
        while (++start < len) {
            if (buf[start] != this.mIdValue.charAt(i)) {
                return false;
            }
            ++i;
        }
        return true;
    }
    
    public boolean idMatches(final String idStr) {
        return this.mIdValue.equals(idStr);
    }
    
    public ElementId nextUndefined() {
        return this.mNextUndefined;
    }
    
    public ElementId nextColliding() {
        return this.mNextColl;
    }
    
    public void markDefined(final Location defLoc) {
        if (this.mDefined) {
            throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
        }
        this.mDefined = true;
        this.mLocation = defLoc;
    }
    
    @Override
    public String toString() {
        return this.mIdValue;
    }
}
