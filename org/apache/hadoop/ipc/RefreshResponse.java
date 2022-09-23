// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
public class RefreshResponse
{
    private int returnCode;
    private String message;
    private String senderName;
    
    public static RefreshResponse successResponse() {
        return new RefreshResponse(0, "Success");
    }
    
    public RefreshResponse(final int returnCode, final String message) {
        this.returnCode = -1;
        this.returnCode = returnCode;
        this.message = message;
    }
    
    public void setSenderName(final String name) {
        this.senderName = name;
    }
    
    public String getSenderName() {
        return this.senderName;
    }
    
    public int getReturnCode() {
        return this.returnCode;
    }
    
    public void setReturnCode(final int rc) {
        this.returnCode = rc;
    }
    
    public void setMessage(final String m) {
        this.message = m;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        String ret = "";
        if (this.senderName != null) {
            ret = ret + this.senderName + ": ";
        }
        if (this.message != null) {
            ret += this.message;
        }
        ret = ret + " (exit " + this.returnCode + ")";
        return ret;
    }
}
