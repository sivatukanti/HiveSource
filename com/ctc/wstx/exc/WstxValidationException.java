// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.Location;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationException;

public class WstxValidationException extends XMLValidationException
{
    protected WstxValidationException(final XMLValidationProblem cause, final String msg) {
        super(cause, msg);
    }
    
    protected WstxValidationException(final XMLValidationProblem cause, final String msg, final Location loc) {
        super(cause, msg, loc);
    }
    
    public static WstxValidationException create(final XMLValidationProblem cause) {
        final Location loc = cause.getLocation();
        if (loc == null) {
            return new WstxValidationException(cause, cause.getMessage());
        }
        return new WstxValidationException(cause, cause.getMessage(), loc);
    }
    
    @Override
    public String getMessage() {
        final String locMsg = this.getLocationDesc();
        if (locMsg == null) {
            return super.getMessage();
        }
        final String msg = this.getValidationProblem().getMessage();
        final StringBuilder sb = new StringBuilder(msg.length() + locMsg.length() + 20);
        sb.append(msg);
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
