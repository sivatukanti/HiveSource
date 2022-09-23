// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class WstxException extends XMLStreamException
{
    protected final String mMsg;
    
    public WstxException(final String msg) {
        super(msg);
        this.mMsg = msg;
    }
    
    public WstxException(final Throwable th) {
        super(th.getMessage(), th);
        this.mMsg = th.getMessage();
    }
    
    public WstxException(final String msg, final Location loc) {
        super(msg, loc);
        this.mMsg = msg;
    }
    
    public WstxException(final String msg, final Location loc, final Throwable th) {
        super(msg, loc, th);
        this.mMsg = msg;
    }
    
    @Override
    public String getMessage() {
        final String locMsg = this.getLocationDesc();
        if (locMsg == null) {
            return super.getMessage();
        }
        final StringBuilder sb = new StringBuilder(this.mMsg.length() + locMsg.length() + 20);
        sb.append(this.mMsg);
        StringUtil.appendLF(sb);
        sb.append(" at ");
        sb.append(locMsg);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.getMessage();
    }
    
    protected String getLocationDesc() {
        final Location loc = this.getLocation();
        return (loc == null) ? null : loc.toString();
    }
}
