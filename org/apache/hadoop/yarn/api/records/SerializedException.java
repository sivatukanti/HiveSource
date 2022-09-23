// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class SerializedException
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static SerializedException newInstance(final Throwable e) {
        final SerializedException exception = Records.newRecord(SerializedException.class);
        exception.init(e);
        return exception;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void init(final String p0, final Throwable p1);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void init(final String p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void init(final Throwable p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract String getMessage();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract String getRemoteTrace();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract SerializedException getCause();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract Throwable deSerialize();
    
    private void stringify(final StringBuilder sb) {
        sb.append(this.getMessage()).append("\n").append(this.getRemoteTrace());
        final SerializedException cause = this.getCause();
        if (cause != null) {
            sb.append("Caused by: ");
            cause.stringify(sb);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        this.stringify(sb);
        return sb.toString();
    }
}
