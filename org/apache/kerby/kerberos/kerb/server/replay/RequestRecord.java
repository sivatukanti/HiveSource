// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.replay;

public class RequestRecord
{
    private String clientPrincipal;
    private String serverPrincipal;
    private long requestTime;
    private int microseconds;
    
    public RequestRecord(final String clientPrincipal, final String serverPrincipal, final long requestTime, final int microseconds) {
        this.clientPrincipal = clientPrincipal;
        this.serverPrincipal = serverPrincipal;
        this.requestTime = requestTime;
        this.microseconds = microseconds;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final RequestRecord that = (RequestRecord)o;
        return this.microseconds == that.microseconds && this.requestTime == that.requestTime && this.clientPrincipal.equals(that.clientPrincipal) && this.serverPrincipal.equals(that.serverPrincipal);
    }
    
    @Override
    public int hashCode() {
        int result = this.clientPrincipal.hashCode();
        result = 31 * result + this.serverPrincipal.hashCode();
        result = 31 * result + (int)(this.requestTime ^ this.requestTime >>> 32);
        result = 31 * result + this.microseconds;
        return result;
    }
}
